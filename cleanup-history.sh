#!/bin/bash
# Complete Git History Cleanup - Force Push Script
# ================================================
# This script completes the removal of large files from git history
# by force pushing the cleaned history to GitHub.
#
# ⚠️  WARNING: This is a destructive operation!
# - All commit SHAs will change
# - All collaborators must re-clone or reset their repositories
# - All open PRs will need rebasing
#
# Run this script from a location where you have GitHub credentials configured.

set -e  # Exit on error

echo "======================================================================"
echo "  Git History Cleanup - Force Push Script"
echo "======================================================================"
echo ""
echo "This script will:"
echo "  1. Clone a fresh copy of the repository"
echo "  2. Remove Web-project(travelling) directory from all history"
echo "  3. Force push the cleaned history to GitHub"
echo ""
echo "⚠️  WARNING: This will rewrite git history!"
echo "   All collaborators must re-clone after this completes."
echo ""
read -p "Are you sure you want to continue? (yes/no): " confirm

if [ "$confirm" != "yes" ]; then
    echo "Aborted."
    exit 1
fi

echo ""
echo "Step 1: Installing git-filter-repo..."
pip3 install --quiet git-filter-repo || pip install --quiet git-filter-repo

echo ""
echo "Step 2: Cloning repository..."
WORK_DIR="/tmp/repo-cleanup-$$"
mkdir -p "$WORK_DIR"
cd "$WORK_DIR"
git clone https://github.com/farout101/random-codes.git
cd random-codes

echo ""
echo "Step 3: Checking current size..."
echo "Before cleanup:"
git count-objects -vH | grep "size-pack"
du -sh .git

echo ""
echo "Step 4: Removing Web-project(travelling) from history..."
git filter-repo --path "Web-project(travelling)" --invert-paths --force

echo ""
echo "Step 5: Verifying size reduction..."
echo "After cleanup:"
git count-objects -vH | grep "size-pack"
du -sh .git

echo ""
echo "Step 6: Force pushing to GitHub..."
git remote add origin https://github.com/farout101/random-codes.git
git push origin --all --force
git push origin --tags --force

echo ""
echo "======================================================================"
echo "✅ Success! Git history has been cleaned and pushed."
echo "======================================================================"
echo ""
echo "Next steps for collaborators:"
echo "  1. Save any uncommitted work"
echo "  2. Re-clone the repository:"
echo "     cd .. && rm -rf random-codes"
echo "     git clone https://github.com/farout101/random-codes.git"
echo ""
echo "  Or reset existing clone:"
echo "     git fetch --all"
echo "     git reset --hard origin/main"
echo "     git clean -fdx"
echo ""
echo "Cleanup complete! You can now delete: $WORK_DIR"
echo "======================================================================"
