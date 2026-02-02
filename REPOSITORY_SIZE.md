# Repository Size Issue

## Problem Summary

When cloning or pulling this repository, the transfer size is **~30MB** (specifically 29.02 MiB in pack files), while the working directory contains only **520KB** of code files.

## Root Cause

A `Web-project(travelling)` directory containing large image files was previously committed to the repository and later deleted. Although these files no longer exist in the current working directory, they remain in the git history, causing the repository size bloat.

### Large Files in History

The following large files exist in git history:

| File | Size | Status |
|------|------|--------|
| `Web-project(travelling)/landmark.jpg` | 15.1 MB | Deleted but in history |
| `Web-project(travelling)/Packages/img/Bagan.png` | 2.3 MB | Deleted but in history |
| `Web-project(travelling)/baganNight.png` | 1.7 MB | Deleted but in history |
| `Web-project(travelling)/dall-e.png` | 1.7 MB | Deleted but in history |
| `Web-project(travelling)/zayti.png` | 1.7 MB | Deleted but in history |
| Other images | ~5 MB | Deleted but in history |

**Total:** ~27+ MB of image files in git history

These files were deleted in commit `9887c66` ("Delete Web-project(travelling) directory") but remain in the repository's history.

## Impact

- **Fresh clones:** Users downloading the repository for the first time must transfer ~30MB instead of ~520KB
- **Network usage:** 58x more data transferred than necessary
- **Storage:** Each clone stores an extra ~29MB in `.git/objects`
- **CI/CD:** Slower build times due to longer clone operations

## Solutions

### Option 1: Shallow Clone (Recommended for Users)

Users can clone only the recent history to avoid downloading old commits:

```bash
git clone --depth 1 https://github.com/farout101/random-codes.git
```

This reduces the clone size from 30MB to less than 1MB.

### Option 2: Clean Git History (Repository Maintainer)

⚠️ **Warning:** This rewrites git history and requires force-pushing. All contributors must re-clone.

To permanently remove large files from history:

#### Using BFG Repo-Cleaner (Recommended)

```bash
# 1. Download BFG from https://rtyley.github.io/bfg-repo-cleaner/
# 2. Clone a fresh copy with mirror
git clone --mirror https://github.com/farout101/random-codes.git

# 3. Remove large files
java -jar bfg.jar --delete-files "landmark.jpg" random-codes.git
java -jar bfg.jar --delete-folders "Web-project(travelling)" random-codes.git

# 4. Clean up
cd random-codes.git
git reflog expire --expire=now --all
git gc --prune=now --aggressive

# 5. Force push (⚠️ requires all collaborators to re-clone)
git push --force
```

#### Using git filter-repo (Alternative)

```bash
# 1. Install git-filter-repo: pip install git-filter-repo
git clone https://github.com/farout101/random-codes.git
cd random-codes

# 2. Remove the directory from history
git filter-repo --path "Web-project(travelling)" --invert-paths

# 3. Force push (⚠️ requires all collaborators to re-clone)
git remote add origin https://github.com/farout101/random-codes.git
git push --force --all
git push --force --tags
```

### Option 3: Keep As-Is with Documentation

Accept the current state and document it for users. Large files remain in history but users are aware and can use shallow clones.

## Prevention

The `.gitignore` file has been updated to prevent future commits of large binary files:

- Image files: `*.jpg`, `*.png`, `*.gif`, etc.
- Video files: `*.mp4`, `*.avi`, `*.mov`, etc.
- Archive files: `*.zip`, `*.tar`, `*.rar`, etc.

### Best Practices

1. **Never commit large binary files** to the repository
2. **Use Git LFS** for necessary large files
3. **Store media files externally** (e.g., CDN, cloud storage)
4. **Review file sizes** before committing: `git diff --stat`
5. **Use pre-commit hooks** to block large files

## Current Status

- ✅ Issue identified and documented
- ✅ `.gitignore` updated to prevent future large file commits
- ⏸️ History cleaning is optional and requires maintainer decision

## Questions?

If you have questions about repository size or need help with shallow clones, please open an issue.
