COMPILATION

    To compile "WcfCmisWSTests" project you need:
        1) install .NET SDK v3.5 to your system;
        2) copy full "WcfCmisWSTests" project to some directory (e.g.: "C:\WcfCmisWSTests");
        3) check if the .NET SDK location and "SDK_LOCATION" variable value in the "build.bat" file are the same
           and introduce neccessary corrections;
        4) run "build.bat" file.
    After compilation finished "build" folder should be created in the project directory. This folder will
    contain executable and configuration files.

TESTS RUNNING

    To run tests you just need the following 2 files:
        WcfCmisWSTests.exe
        WcfCmisWSTests.exe.config        
    Configure environment in "WcfCmisWSTests.exe.config" file and execute "WcfCmisWSTests.exe".
    All information about tests execution will be displayed in the console window.
