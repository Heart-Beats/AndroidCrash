cmake_minimum_required(VERSION 3.4.1)

project("breakpad-android")

set(BREAKPAD_ROOT .)
include_directories(${BREAKPAD_ROOT})
file(GLOB BREAKPAD_SOURCES_COMMON
        breakpad.cpp
        ${BREAKPAD_ROOT}/client/linux/minidump_whole_writer/minidump_whole_track_writer.cc
        ${BREAKPAD_ROOT}/third_party/utils/libunwind/libunwind_utils.cpp
        ${BREAKPAD_ROOT}/third_party/utils/android_version_utils.cpp
        ${BREAKPAD_ROOT}/third_party/utils/crash_info_utils.cpp
        ${BREAKPAD_ROOT}/third_party/utils/ndk_dlopen/dlopen.c
        ${BREAKPAD_ROOT}/third_party/utils/libbacktrace/libbacktrace_utils.cpp

        ${BREAKPAD_ROOT}/client/linux/crash_generation/crash_generation_client.cc
        ${BREAKPAD_ROOT}/client/linux/dump_writer_common/thread_info.cc
        ${BREAKPAD_ROOT}/client/linux/dump_writer_common/ucontext_reader.cc
        ${BREAKPAD_ROOT}/client/linux/handler/exception_handler.cc
        ${BREAKPAD_ROOT}/client/linux/handler/minidump_descriptor.cc
        ${BREAKPAD_ROOT}/client/linux/log/log.cc
        ${BREAKPAD_ROOT}/client/linux/microdump_writer/microdump_writer.cc
        ${BREAKPAD_ROOT}/client/linux/minidump_writer/linux_dumper.cc
        ${BREAKPAD_ROOT}/client/linux/minidump_writer/linux_ptrace_dumper.cc
        ${BREAKPAD_ROOT}/client/linux/minidump_writer/minidump_writer.cc
        ${BREAKPAD_ROOT}/client/minidump_file_writer.cc
        ${BREAKPAD_ROOT}/common/convert_UTF.cc
        ${BREAKPAD_ROOT}/common/md5.cc
        ${BREAKPAD_ROOT}/common/string_conversion.cc
        ${BREAKPAD_ROOT}/common/linux/elfutils.cc
        ${BREAKPAD_ROOT}/common/linux/file_id.cc
        ${BREAKPAD_ROOT}/common/linux/guid_creator.cc
        ${BREAKPAD_ROOT}/common/linux/linux_libc_support.cc
        ${BREAKPAD_ROOT}/common/linux/memory_mapped_file.cc
        ${BREAKPAD_ROOT}/common/linux/safe_readlink.cc
)

file(GLOB BREAKPAD_ASM_SOURCE ${BREAKPAD_ROOT}/common/linux/breakpad_getcontext.S)
set_source_files_properties(${BREAKPAD_ASM_SOURCE} PROPERTIES LANGUAGE C)

add_library(${CMAKE_PROJECT_NAME} SHARED ${BREAKPAD_SOURCES_COMMON} ${BREAKPAD_ASM_SOURCE})

find_library(log-lib log)

target_link_libraries(${CMAKE_PROJECT_NAME} ${log-lib})
