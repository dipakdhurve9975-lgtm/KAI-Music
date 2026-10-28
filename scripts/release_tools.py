#!/usr/bin/env python3
"""KAI release validation and download generation. Python standard library only.

Modified for KAI Music on 2026-10-28; GPL-3.0. Never publishes releases.
"""
import argparse
import hashlib
import json
import os
from pathlib import Path, PurePosixPath
import re
import subprocess
import urllib.error
import urllib.request
import zipfile

REPO = "dipakdhurve9975-lgtm/KAI-Music"
BASE = f"https://github.com/{REPO}"
START = "<!-- kai-downloads:start -->"
END = "<!-- kai-downloads:end -->"
FORBIDDEN_PARTS = {".git", ".gradle", ".kotlin", ".cxx", ".idea", "build", "__pycache__"}
FORBIDDEN_NAMES = {"local.properties", "google-services.json", "keystore.properties", "signing.properties"}
FORBIDDEN_SUFFIXES = {".keystore", ".jks", ".p12", ".p8", ".pem", ".apk", ".aab", ".log", ".pyc"}


def allowed_source(name):
    p = PurePosixPath(name)
    return (not p.is_absolute() and ".." not in p.parts and ":" not in name and "\\" not in name
            and not FORBIDDEN_PARTS.intersection(p.parts)
            and p.name not in FORBIDDEN_NAMES
            and p.suffix.lower() not in FORBIDDEN_SUFFIXES
            and not p.name.startswith(".env"))


def version(root):
    text = (Path(root) / "app/build.gradle.kts").read_text(encoding="utf-8")
    name = re.search(r'versionName\s*=\s*"(\d+\.\d+\.\d+)"', text)
    code = re.search(r'versionCode\s*=\s*(\d+)', text)
    if not name or not code:
        raise ValueError("Missing literal semantic version/versionCode")
    return name[1], int(code[1])


def validate_tag(root, tag, channel):
    name, code = version(root)
    expected = f"kai-v{name}" if channel == "stable" else f"kai-preview-{name}"
    if channel not in {"stable", "preview"} or tag != expected:
        raise ValueError(f"Tag must match source version and channel: {expected}")
    return name, code


def api(path):
    headers = {"User-Agent": "KAI-release-tools", "Accept": "application/vnd.github+json"}
    if os.environ.get("GH_TOKEN"):
        headers["Authorization"] = "Bearer " + os.environ["GH_TOKEN"]
    with urllib.request.urlopen(urllib.request.Request("https://api.github.com" + path, headers=headers), timeout=30) as response:
        return json.load(response)


def release_asset(release, filename):
    if release.get("draft"):
        return None
    for asset in release.get("assets", []):
        url = asset.get("browser_download_url", "")
        expected_url = BASE + "/releases/download/" + release.get("tag_name", "") + "/" + filename
        if (asset.get("name") == filename and asset.get("state", "uploaded") == "uploaded"
                and asset.get("size", 0) > 0 and url == expected_url):
            return url
    return None


def select_preview(releases):
    candidates = []
    for release in releases:
        match = re.fullmatch(r"kai-preview-(\d+)\.(\d+)\.(\d+)", release.get("tag_name", ""))
        # The legacy kai-preview-2026.10.27 date tag is not a higher app version.
        if release.get("prerelease") and match and int(match[1]) < 2000:
            asset = release_asset(release, "KAI-universal-gms-preview.apk")
            if asset:
                candidates.append((tuple(map(int, match.groups())), release, asset))
    return max(candidates, key=lambda x: x[0]) if candidates else None


def download_section(releases, latest_stable=None):
    lines = []
    stable = latest_stable and not latest_stable.get("prerelease") and release_asset(latest_stable, "KAI-latest.apk")
    if stable:
        lines += [f"[**Download Latest APK — stable**]({BASE}/releases/latest/download/KAI-latest.apk)", ""]
    else:
        lines += ["**No stable KAI release is available yet.** The current download is a debug-signed preview, not a stable build.", ""]
    preview = select_preview(releases)
    if preview:
        _, release, url = preview
        lines += [f"[![Download KAI preview APK](assets/download.svg)]({url})", "",
                  f"Current preview: **{release['tag_name'].removeprefix('kai-preview-')}**, Universal GMS, Android 8.0+.", ""]
        source_name = "KAI-Music-source-" + release['tag_name'].removeprefix('kai-preview-') + ".zip"
        source = next((a for a in release.get("assets", []) if a.get("name") == source_name and release_asset(release, source_name)), None)
        if source:
            lines += [f"[Matching preview source ZIP]({source['browser_download_url']})", ""]
    lines += [f"[View All Releases]({BASE}/releases) · [Source Code]({BASE})", "",
              "Universal includes ARM64, ARMv7, x86 and x86_64 native libraries. GMS enables Google Cast; Google-dependent features need compatible services. No non-GMS APK is currently published."]
    return "\n".join(lines)


def update_downloads(root):
    releases = []
    for page in range(1, 11):
        batch = api(f"/repos/{REPO}/releases?per_page=100&page={page}")
        releases.extend(batch)
        if len(batch) < 100:
            break
    try:
        stable = api(f"/repos/{REPO}/releases/latest")
    except urllib.error.HTTPError as error:
        if error.code != 404:
            raise
        stable = None
    path = Path(root) / "README.md"
    text = path.read_text(encoding="utf-8")
    if text.count(START) != 1 or text.count(END) != 1 or text.index(START) > text.index(END):
        raise ValueError("Expected exactly one ordered download marker pair")
    before, remainder = text.split(START)
    _, after = remainder.split(END)
    updated = before + START + "\n" + download_section(releases, stable) + "\n" + END + after
    if updated != text:
        path.write_text(updated, encoding="utf-8", newline="\n")


def git(root, *args):
    return subprocess.check_output(["git", "-C", str(root), *args])


def scan(root, names):
    """Conservative leak gate; reports file/line/type only, never matching values."""
    findings = []
    allow_path = Path(root) / "docs/public-value-exceptions.json"
    exceptions = json.loads(allow_path.read_text()) if allow_path.exists() else []
    patterns = {
        "private-key": re.compile(r"-----BEGIN (?:RSA |EC |OPENSSH )?PRIVATE KEY-----"),
        "github-token": re.compile(r"\b(?:gh[pousr]_[A-Za-z0-9]{30,}|github_pat_[A-Za-z0-9_]{30,})\b"),
        "aws-access-key": re.compile(r"\bAKIA[0-9A-Z]{16}\b"),
        "literal-service-credential": re.compile(r'''(?i)(?:api[_-]?key|client[_-]?secret|lastfmsecret|lastfmkey|password|access[_-]?token)\s*(?:=|:)\s*["'][A-Za-z0-9_+/=-]{16,}["']'''),
        "credential-in-url": re.compile(r"https?://[^\s/]+:[^\s/@]+@"),
    }
    for name in names:
        if not allowed_source(name):
            findings.append({"file": name, "kind": "private/build file"})
            continue
        data = (Path(root) / name).read_bytes()
        if b"\0" in data:
            continue
        try:
            text = data.decode("utf-8")
        except UnicodeDecodeError:
            continue
        for i, line in enumerate(text.splitlines(), 1):
            for kind, pattern in patterns.items():
                match = pattern.search(line)
                approved = match and any(
                    item.get("file") == name and item.get("kind") == kind
                    and item.get("match_sha256") == hashlib.sha256(match[0].encode()).hexdigest()
                    for item in exceptions)
                if match and not approved:
                    findings.append({"file": name, "line": i, "kind": kind})
    return findings


def tracked(root):
    return [n.decode("utf-8") for n in git(root, "ls-files", "-z").split(b"\0") if n]


def source_archive(root, destination):
    # Archive the exact committed revision, never a directory containing local credentials.
    if git(root, "status", "--porcelain", "--untracked-files=no").strip():
        raise ValueError("Tracked source must be clean before creating a release archive")
    names = tracked(root)
    findings = scan(root, names)
    if findings:
        raise ValueError("Source scan failed: " + json.dumps(findings))
    subprocess.run(["git", "-C", str(root), "archive", "--format=zip", "--prefix=KAI-Music/", "-o", str(Path(destination).resolve()), "HEAD"], check=True)


def sha256(path):
    digest = hashlib.sha256()
    with Path(path).open("rb") as stream:
        for block in iter(lambda: stream.read(1024 * 1024), b""):
            digest.update(block)
    return digest.hexdigest()


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("command", choices=["version", "validate-tag", "downloads", "scan", "archive"])
    parser.add_argument("--root", type=Path, default=Path(__file__).resolve().parents[1])
    parser.add_argument("--tag")
    parser.add_argument("--channel", choices=["stable", "preview"], default="preview")
    parser.add_argument("--output", type=Path)
    args = parser.parse_args()
    if args.command == "version":
        print(json.dumps(dict(zip(("name", "code"), version(args.root)))))
    elif args.command == "validate-tag":
        print(json.dumps(dict(zip(("name", "code"), validate_tag(args.root, args.tag, args.channel)))))
    elif args.command == "downloads":
        update_downloads(args.root)
    elif args.command == "archive":
        source_archive(args.root, args.output)
    else:
        findings = scan(args.root, tracked(args.root))
        print(json.dumps(findings, indent=2))
        raise SystemExit(bool(findings))


if __name__ == "__main__":
    main()
