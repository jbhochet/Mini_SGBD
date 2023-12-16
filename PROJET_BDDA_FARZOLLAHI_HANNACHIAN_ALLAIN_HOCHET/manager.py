import argparse
import os
import shutil
import subprocess
from os import path


###############
# Functions
###############

def list_dir(dir_path, filters=None, files=None):
    """
    Return a list of all file path in this directory
    If filter is specified, file name must match the filter
    """
    if files is None:
        files = []
    directories = os.scandir(dir_path)
    for directory in directories:
        if directory.is_file():
            if filters is None or filters(directory.name):
                files.append(directory.path)
        else:
            files += list_dir(directory.path)
    return files


def compile_java(src, output_dir, classpath, version):
    """
    Compile java source code in src and output class files in bin directory
    src is the source path with java files
    bin is the binary path with class files
    classpath is the list of path to include in javac classpath
    version is the java version used to compile source code
    """
    files = list_dir(src, filters=lambda f: f.endswith(".java"))
    classpath = ":".join(classpath)
    subprocess.run(["javac", "--release", str(version), "--source-path", src, "-cp", classpath, "-d", output_dir]
                   + files)
    pass


###############
# Script
###############

JAVA_VERSION = 17
SOURCE_DIR = "CODE/src"
BIN_DIR = "CODE/bin"
MAIN_CLASS = "Main"

# Setup command line argument

parser = argparse.ArgumentParser(description="SGBD/DBMS Manager (◠﹏◠)")

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
compile_java(path.join(SOURCE_DIR, "main"), BIN_DIR, [], JAVA_VERSION)

# Compile tests files and run tests
if args.test:
    compile_java(path.join(SOURCE_DIR, "test"), BIN_DIR, [BIN_DIR], JAVA_VERSION)

# Run java code
subprocess.run(["java", "-cp", BIN_DIR, MAIN_CLASS, dbpath])
