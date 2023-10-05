import subprocess
import sys

my_os=sys.platform

print(my_os)

if my_os=="linux" or my_os=="macOS":
    lscript=subprocess.run(['bash','compile_run.bash','../../DB'])
    print("Completed !")
else:
    wscript=subprocess.run(['batch','compile_and_run.bat','..\..\DB'])
    print("Completed")
