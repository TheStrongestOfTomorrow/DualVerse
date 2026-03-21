cmake_minimum_required(VERSION 3.22.1)

project("dualverse-native")

# Set C++ standard
set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

# Optimization flags
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -O3 -ffast-math")
set(CMAKE_CXX_FLAGS_DEBUG "-g -DDEBUG")

# Add native library
add_library(
    dualverse-native
    SHARED
    native.cpp
    container.cpp
    memory_bridge.cpp
    network_namespace.cpp
    device_spoofer.cpp
    storage_isolator.cpp
)

# Find required libraries
find_library(log-lib log)
find_library(android-lib android)

# Link libraries
target_link_libraries(
    dualverse-native
    ${log-lib}
    ${android-lib}
)
