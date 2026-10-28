#!/usr/bin/env python3
"""Collect available resolved dependency notices; unknown obligations remain explicit.

Modified for KAI Music on 2026-10-28; GPL-3.0. Not a legal certification.
"""
import argparse
import hashlib
import io
import json
import os
from pathlib import Path
import re
import xml.etree.ElementTree as ET
import zipfile


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--root", type=Path, default=Path(__file__).resolve().parents[1])
    args = parser.parse_args()
    root = args.root
    artifacts = json.loads((root / "build/kai-runtime-artifacts.json").read_text())
    cache = Path(os.environ.get("GRADLE_USER_HOME", Path.home() / ".gradle")) / "caches/modules-2/files-2.1"
    notices = root / "app/src/main/assets/licenses/dependencies"
    notices.mkdir(parents=True, exist_ok=True)
    result = []
    def archive_notices(data, files, depth=0):
        try:
            with zipfile.ZipFile(io.BytesIO(data)) as archive:
                for entry in archive.infolist():
                    name = Path(entry.filename).name.lower()
                    if entry.is_dir() or entry.file_size > 1024 * 1024:
                        continue
                    if depth == 0 and entry.filename == "classes.jar":
                        archive_notices(archive.read(entry), files, 1)
                    if not re.fullmatch(r"(?:license|licence|notice|copying)(?:[._-].*)?", name):
                        continue
                    content = archive.read(entry)
                    try:
                        content.decode("utf-8")
                    except UnicodeDecodeError:
                        continue
                    digest = hashlib.sha256(content).hexdigest()
                    (notices / f"{digest}.txt").write_bytes(content)
                    files.append(f"licenses/dependencies/{digest}.txt")
        except zipfile.BadZipFile:
            pass
    for artifact in artifacts:
        coordinate = artifact["coordinate"]
        path = Path(artifact["file"])
        group, name, version = coordinate.split(":", 2)
        licenses = []
        source_url = None
        for pom in (cache / group / name / version).glob("*/*.pom"):
            try:
                element = ET.parse(pom).getroot()
                ns = {"m": "http://maven.apache.org/POM/4.0.0"}
                licenses = [{"name": e.findtext("m:name", namespaces=ns), "url": e.findtext("m:url", namespaces=ns)} for e in element.findall("m:licenses/m:license", ns)]
                source_url = element.findtext("m:scm/m:url", namespaces=ns)
                if licenses:
                    break
            except ET.ParseError:
                continue
        files = []
        archive_notices(path.read_bytes(), files)
        result.append({"coordinate": coordinate, "artifact": path.name,
                       "sha256": hashlib.sha256(path.read_bytes()).hexdigest(),
                       "declared_licenses": licenses, "source_url": source_url,
                       "bundled_notices": sorted(set(files)),
                       "review": "POM declarations are evidence only; check native/transitive obligations" if licenses else "Unresolved: no direct POM license declaration found"})
    (root / "docs/dependency-inventory.json").write_text(json.dumps({"variant": "universalGmsDebug", "artifacts": result}, indent=2) + "\n", encoding="utf-8")
    print(json.dumps({"artifacts": len(result), "with_declared_license": sum(bool(r['declared_licenses']) for r in result), "bundled_notice_files": len(list(notices.glob('*.txt')))}))


if __name__ == "__main__":
    main()
