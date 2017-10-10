cmake_minimum_required(VERSION 3.4.1)

add_library( # Sets the name of the library.
             stringFromJNI-lib

             # Sets the library as a shared library.
             SHARED

             # Provides a relative path to your source file(s).
             src/main/cpp/stringfromJNI.cpp )


find_library( log-lib
              log )


target_link_libraries( # Specifies the target library.
                       stringFromJNI-lib

                       # Links the target library to the log library
                       # included in the NDK.
                       ${log-lib} )