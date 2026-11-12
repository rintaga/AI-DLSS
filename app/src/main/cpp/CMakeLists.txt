cmake_minimum_required(VERSION 3.22.1)
project(AIDLSS)

include(FetchContent)

FetchContent_Declare(
    imgui
    GIT_REPOSITORY https://github.com/ocornut/imgui.git
    GIT_TAG v1.91.8
    GIT_SHALLOW TRUE
)

FetchContent_GetProperties(imgui)

if(NOT imgui_POPULATED)
    FetchContent_Populate(imgui)
endif()

add_library(dlss SHARED
    main.cpp
    BoostEngine.cpp
    ${imgui_SOURCE_DIR}/imgui.cpp
    ${imgui_SOURCE_DIR}/imgui_draw.cpp
    ${imgui_SOURCE_DIR}/imgui_tables.cpp
    ${imgui_SOURCE_DIR}/imgui_widgets.cpp
    ${imgui_SOURCE_DIR}/backends/imgui_impl_opengl3.cpp
)

target_include_directories(dlss PRIVATE
    ${imgui_SOURCE_DIR}
    ${imgui_SOURCE_DIR}/backends
)

find_library(log-lib log)
find_library(gles-lib GLESv3)
find_library(android-lib android)

target_link_libraries(dlss
    ${log-lib}
    ${gles-lib}
    ${android-lib}
)
