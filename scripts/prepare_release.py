#!/usr/bin/env python3
"""Validate a signed APK and bundle its exact committed source. Never publishes.

Modified for KAI Music on 2026-10-28; GPL-3.0.
"""
import argparse
import json
import os
from pathlib import Path
import re
import shutil
import subprocess
from release_tools import git, sha256, source_archive, validate_tag


def apk_metadata(badging):
    package = re.search(r"package: name='([^']+)' versionCode='(\d+)' versionName='([^']+)'", badging)
    if not package:
        raise ValueError("Cannot parse APK package/version")
    sdk = re.search(r"sdkVersion:'(\d+)'", badging)
    native = re.search(r"native-code:(.*)", badging)
    return {"application_id": package[1], "version_code": int(package[2]), "version_name": package[3],
            "min_sdk": int(sdk[1]) if sdk else None,
            "abis": re.findall(r"'([^']+)'", native[1]) if native else [],
            "debuggable": "application-debuggable" in badging}


def validate_apk(metadata, name, code, channel):
    expected = "echo.music.iad1tya" + (".debug" if channel == "preview" else "")
    if metadata["application_id"] != expected or metadata["version_code"] != code or metadata["version_name"] != name:
        raise ValueError("APK identity/version differs from source/channel")
    if metadata["debuggable"] != (channel == "preview"):
        raise ValueError("Debug/release channel mismatch")
    if metadata["min_sdk"] != 26 or set(metadata["abis"]) != {"arm64-v8a", "armeabi-v7a", "x86", "x86_64"}:
        raise ValueError("Not the intended Android 8.0+ universal APK")


def verify_fingerprint(output, expected):
    values = re.findall(r"Signer #\d+ certificate SHA-256 digest:\s*([0-9a-fA-F:]+)", output)
    normalized = re.sub(r"[^0-9a-f]", "", expected.lower())
    if len(normalized) != 64 or len(values) != 1 or values[0].replace(":", "").lower() != normalized:
        raise ValueError("Signing certificate mismatch or expected fingerprint missing")
    return normalized


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--root", type=Path, default=Path(__file__).resolve().parents[1])
    parser.add_argument("--apk", type=Path, required=True)
    parser.add_argument("--tag", required=True)
    parser.add_argument("--channel", choices=["stable", "preview"], required=True)
    parser.add_argument("--build-tools", type=Path, required=True)
    parser.add_argument("--output", type=Path, required=True)
    args = parser.parse_args()
    name, code = validate_tag(args.root, args.tag, args.channel)
    aapt = args.build_tools / ("aapt.exe" if os.name == "nt" else "aapt")
    signer = args.build_tools / ("apksigner.bat" if os.name == "nt" else "apksigner")
    metadata = apk_metadata(subprocess.check_output([str(aapt), "dump", "badging", str(args.apk)], text=True, encoding="utf-8"))
    validate_apk(metadata, name, code, args.channel)
    signature = subprocess.check_output([str(signer), "verify", "--print-certs", str(args.apk)], text=True)
    metadata["signer_sha256"] = verify_fingerprint(signature, os.environ.get("KAI_EXPECTED_SIGNER_SHA256", ""))
    if code <= int(os.environ.get("KAI_PREVIOUS_VERSION_CODE", "168")):
        raise ValueError("versionCode must exceed the last approved build")
    notes = args.root / "docs/release-notes" / f"{name}.md"
    if not notes.is_file():
        raise ValueError("Write reviewed release notes for this version first")
    if args.channel == "stable":
        compliance = json.loads((args.root / "docs/release-approval.json").read_text())
        if (os.environ.get("KAI_STABLE_RELEASES_ENABLED") != "true"
                or compliance.get("version") != name or not compliance.get("stable_distribution_reviewed")
                or compliance.get("unresolved_blockers")):
            raise ValueError("Stable release review incomplete; never relabel a preview")
    args.output.mkdir(parents=True, exist_ok=True)
    if any(args.output.iterdir()):
        raise ValueError("Output must be empty; artifacts are never overwritten")
    apk_name = f"KAI-{name}-universal-gms-{'release' if args.channel == 'stable' else 'preview'}.apk"
    shutil.copyfile(args.apk, args.output / apk_name)
    alias = "KAI-latest.apk" if args.channel == "stable" else "KAI-universal-gms-preview.apk"
    shutil.copyfile(args.apk, args.output / alias)
    source_archive(args.root, args.output / f"KAI-Music-source-{name}.zip")
    metadata.update({"source_commit": git(args.root, "rev-parse", "HEAD").decode().strip(),
                     "source_tag": args.tag, "channel": args.channel, "variant": "universalGms", "public_build": True,
                     "workflow_run_id": os.environ.get("GITHUB_RUN_ID"), "apk_sha256": sha256(args.apk)})
    (args.output / "build-metadata.json").write_text(json.dumps(metadata, indent=2) + "\n", encoding="utf-8")
    if (args.root / "docs/dependency-inventory.json").exists():
        shutil.copyfile(args.root / "docs/dependency-inventory.json", args.output / "dependency-inventory.json")
    shutil.copyfile(notes, args.output / "RELEASE_NOTES.md")
    files = sorted(args.output.iterdir())
    (args.output / "SHA256SUMS.txt").write_text("".join(f"{sha256(p)}  {p.name}\n" for p in files), encoding="utf-8")
    print(json.dumps(metadata, indent=2))


if __name__ == "__main__":
    main()
