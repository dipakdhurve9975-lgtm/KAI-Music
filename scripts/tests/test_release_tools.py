"""Release safety regressions. Modified for KAI Music on 2026-10-28; GPL-3.0."""
import sys
from pathlib import Path
import subprocess
import tempfile
import unittest
from unittest.mock import patch
import zipfile
sys.path.insert(0, str(Path(__file__).resolve().parents[1]))
import release_tools as rt
import prepare_release as pr


def release(tag="kai-preview-1.4.7", preview=True, filename="KAI-universal-gms-preview.apk", **kwargs):
    return {"tag_name": tag, "prerelease": preview, "draft": False, "assets": [
        {"name": filename, "size": 128, "state": "uploaded", "browser_download_url": rt.BASE + "/releases/download/" + tag + "/" + filename}], **kwargs}


class DownloadPolicyTest(unittest.TestCase):
    def test_preview_is_never_advertised_as_stable(self):
        p = release()
        text = rt.download_section([p], p)
        self.assertIn("No stable", text)
        self.assertNotIn("releases/latest/download", text)

    def test_semantic_order_beats_api_order_and_legacy_date_tag(self):
        rows = [release("kai-preview-2026.10.27"), release("kai-preview-1.4.9"), release("kai-preview-1.4.10")]
        self.assertEqual("kai-preview-1.4.10", rt.select_preview(rows)[1]["tag_name"])

    def test_drafts_empty_and_missing_assets_are_ignored(self):
        broken = release("kai-preview-1.4.9")
        broken["assets"][0]["size"] = 0
        self.assertEqual("kai-preview-1.4.7", rt.select_preview([release(), release("kai-preview-1.4.8", draft=True), broken])[1]["tag_name"])

    def test_unrelated_download_url_is_rejected(self):
        p = release()
        p["assets"][0]["browser_download_url"] = "https://github.com/EchoMusicApp/Echo-Music/releases/download/v1/app.apk"
        self.assertIsNone(rt.select_preview([p]))

    def test_latest_stable_needs_uploaded_alias(self):
        stable = release("kai-v1.4.8", False, "versioned.apk")
        self.assertNotIn("releases/latest/download", rt.download_section([], stable))
        stable = release("kai-v1.4.8", False, "KAI-latest.apk")
        self.assertIn("releases/latest/download/KAI-latest.apk", rt.download_section([], stable))

    def test_refresh_preserves_credits_and_license_outside_markers(self):
        with tempfile.TemporaryDirectory() as d:
            p = Path(d) / "README.md"
            p.write_text("credits\n" + rt.START + "\nold\n" + rt.END + "\nlicense\n")
            with patch.object(rt, "api", side_effect=[[release()], None]):
                rt.update_downloads(d)
            self.assertTrue(p.read_text().startswith("credits\n" + rt.START))
            self.assertTrue(p.read_text().endswith(rt.END + "\nlicense\n"))

    def test_duplicate_markers_fail(self):
        with tempfile.TemporaryDirectory() as d:
            (Path(d) / "README.md").write_text(rt.START + rt.START + rt.END)
            with patch.object(rt, "api", side_effect=[[], None]), self.assertRaises(ValueError):
                rt.update_downloads(d)


class ArtifactPolicyTest(unittest.TestCase):
    def test_private_and_build_paths_excluded(self):
        for path in [".git/config", "app/build/generated/secret.txt", "app/google-services.json", "local.properties", "key.jks", ".env.production", "../escape.kt", "/absolute.kt", "capture.apk"]:
            self.assertFalse(rt.allowed_source(path), path)
        self.assertTrue(rt.allowed_source("app/src/main/kotlin/Player.kt"))

    def test_scan_reports_location_without_value(self):
        with tempfile.TemporaryDirectory() as d:
            value = "ghp_" + "Z" * 36
            (Path(d) / "file.kt").write_text(value)
            result = rt.scan(d, ["file.kt"])
            self.assertEqual("github-token", result[0]["kind"])
            self.assertNotIn(value, str(result))

    def test_tag_matches_version_and_channel(self):
        with tempfile.TemporaryDirectory() as d:
            (Path(d) / "app").mkdir()
            (Path(d) / "app/build.gradle.kts").write_text('versionName = "1.4.8"\nversionCode = 169')
            self.assertEqual(("1.4.8", 169), rt.validate_tag(d, "kai-preview-1.4.8", "preview"))
            for tag in ["kai-v1.4.8", "kai-preview-1.4.7", "refs/tags/evil", "x; echo pwn"]:
                with self.assertRaises(ValueError):
                    rt.validate_tag(d, tag, "preview")

    def test_json_and_unquoted_property_secrets_are_rejected(self):
        with tempfile.TemporaryDirectory() as d:
            import json
            (Path(d) / "config.json").write_text(json.dumps({"api_key": "Z" * 32}))
            (Path(d) / "gradle.properties").write_text("ACCESS_TOKEN=" + "Q" * 32)
            result = rt.scan(d, ["config.json", "gradle.properties"])
            self.assertEqual({"literal-service-credential", "bare-property-credential"}, {r["kind"] for r in result})

    def test_apk_parser_and_channel_validation(self):
        data = pr.apk_metadata("package: name='echo.music.iad1tya.debug' versionCode='169' versionName='1.4.8'\nsdkVersion:'26'\napplication-debuggable\nnative-code: 'arm64-v8a' 'armeabi-v7a' 'x86' 'x86_64'")
        pr.validate_apk(data, "1.4.8", 169, "preview")
        with self.assertRaises(ValueError):
            pr.validate_apk(data, "1.4.8", 169, "stable")
        with self.assertRaises(ValueError):
            pr.validate_apk(data, "1.4.8", 170, "preview")

    def test_exact_single_fingerprint(self):
        fingerprint = "ab" * 32
        self.assertEqual(fingerprint, pr.verify_fingerprint("Signer #1 certificate SHA-256 digest: " + fingerprint, fingerprint))
        for expected in ["", "cd" * 32]:
            with self.assertRaises(ValueError):
                pr.verify_fingerprint("Signer #1 certificate SHA-256 digest: " + fingerprint, expected)
        with self.assertRaises(ValueError):
            pr.verify_fingerprint("Signer #1 certificate SHA-256 digest: " + fingerprint + "\nSigner #2 certificate SHA-256 digest: " + fingerprint, fingerprint)

    def test_updater_metadata_is_generated_from_inspected_apk(self):
        inspected = {
            "application_id": "echo.music.iad1tya.debug", "version_code": 170,
            "version_name": "1.4.9", "min_sdk": 26,
            "abis": ["arm64-v8a", "armeabi-v7a", "x86", "x86_64"],
            "channel": "preview", "variant": "universalGms", "apk_sha256": "ab" * 32,
            "signer_sha256": "cd" * 32, "source_commit": "ef" * 20,
        }
        manifest = pr.update_manifest(inspected, "KAI-universal-gms-preview.apk")
        self.assertEqual(1, manifest["schema_version"])
        self.assertEqual(170, manifest["version_code"])
        self.assertEqual(inspected["apk_sha256"], manifest["apk_sha256"])
        self.assertEqual(inspected["signer_sha256"], manifest["signer_sha256"])
        self.assertEqual(inspected["source_commit"], manifest["source_commit"])
        self.assertEqual("KAI-universal-gms-preview.apk", manifest["apk_file_name"])

    def test_archive_matches_commit_and_rejects_dirty_source(self):
        with tempfile.TemporaryDirectory() as d, tempfile.TemporaryDirectory() as output:
            subprocess.run(["git", "init", "-q", d], check=True)
            p = Path(d) / "source.kt"
            p.write_text("original")
            for args in [("add", "source.kt"), ("-c", "user.name=Test", "-c", "user.email=test@example.org", "commit", "-qm", "test")]:
                rt.git(d, *args)
            destination = Path(output) / "source.zip"
            rt.source_archive(d, destination)
            with zipfile.ZipFile(destination) as z:
                self.assertEqual(b"original", z.read("KAI-Music/source.kt"))
            p.write_text("uncommitted")
            with self.assertRaises(ValueError):
                rt.source_archive(d, destination)


if __name__ == "__main__":
    unittest.main()
