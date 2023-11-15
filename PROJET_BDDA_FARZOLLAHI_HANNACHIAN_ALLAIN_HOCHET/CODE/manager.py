import argparse
import os
import shutil
import subprocess
from os import path

###############
# Functions
###############

def listDir(dirPath, filter = None, files = []):
    """
    Return a list of all file path in this directory
    If filter is specified, file name must match the filter
    """
    directories = os.scandir(dirPath)
    for dir in directories:
        if dir.is_file():
            if filter == None or filter(dir.name):
                files.append(dir.path)
        else:
            files += listDir(dir.path)
    return files

def compileJava(src, bin, classpath, version):
    """
    Compile java source code in src and output class files in bin directory
    src is the source path with java files
    bin is the binary path whie class files
    classpath is the list of path to include in javac classpath
    version is the java version used to compile source code

    """
    files = listDir(src, filter=lambda f: f.endswith(".java"))
    classpath = ":".join(classpath)
    subprocess.run(["javac", "--release", str(version), "--source-path", src, "-cp", classpath, "-d", bin] + files)
    pass


###############
# Script
###############

JAVA_VERSION = 17
SOURCE_DIR = "src"
BIN_DIR = "bin"
MAIN_CLASS = "Main"

# Setup command line argument

parser = argparse.ArgumentParser(description="SGBD Manager (◠﹏◠)")

parser.add_argument("dbpath", help="The path to the DB folder")

parser.add_argument("--main-class", help="Run this class instead of the default.")

parser.add_argument("--test", action="store_true", help="Add this flag for compile and run Junit tests")

args = parser.parse_args()

if args.main_class is not None:
    MAIN_CLASS = args.main_class

dbpath = path.realpath(args.dbpath)

# Change directory
os.chdir(path.dirname(path.realpath(__file__)))

# Clear bin directory
shutil.rmtree(BIN_DIR, True)

# Compile source files
compileJava(path.join(SOURCE_DIR, "main"), BIN_DIR, [], JAVA_VERSION)

# Compile tests files and run junit
if(args.test):
    compileJava(path.join(SOURCE_DIR, "test"), BIN_DIR, [BIN_DIR], JAVA_VERSION)

# Run java code
subprocess.run(["java", "-cp", BIN_DIR, MAIN_CLASS, dbpath])
