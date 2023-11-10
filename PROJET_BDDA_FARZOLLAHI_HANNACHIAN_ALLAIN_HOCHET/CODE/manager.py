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
LIB_DIR = "lib"
JUNIT_JAR = "junit-platform-console-standalone-1.9.3.jar"
MAIN_CLASS = "Main"

# Setup command line argument

parser = argparse.ArgumentParser(description="SGBD Manager (◠﹏◠)")

parser.add_argument("dbpath", help="The path to the DB folder")

parser.add_argument("--test", action="store_true", help="Add this flag for compile and run Junit tests")

args = parser.parse_args()

dbpath = path.realpath(args.dbpath)

# Change directory
os.chdir(path.dirname(path.realpath(__file__)))

# Clear bin directory
shutil.rmtree(bin, True)

# Compile source files
compileJava(path.join(SOURCE_DIR, "main"), BIN_DIR, [], JAVA_VERSION)

# Compile tests files and run junit
if(args.test):
    libs = listDir(LIB_DIR, lambda f: f.endswith(".jar"), [])
    compileJava(path.join(SOURCE_DIR, "test"), BIN_DIR, libs + [BIN_DIR], JAVA_VERSION)
    result = subprocess.run(["java", "-jar", path.join(LIB_DIR, JUNIT_JAR), "-cp", BIN_DIR, "--scan-classpath"])
    if result.returncode != 0:
        if(input("Do you want to continue? [Y] ") != 'Y'):
            exit(0)

# Run java code
subprocess.run(["java", "-cp", BIN_DIR, MAIN_CLASS, dbpath])