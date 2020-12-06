# Copyright (C) 2014 The Android Open Source Project
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_PACKAGE_NAME := OdroidSettings
LOCAL_CERTIFICATE := platform
LOCAL_MODULE_TAGS := optional
LOCAL_PROGUARD_FLAG_FILES := proguard.cfg
LOCAL_USE_AAPT2 := true
LOCAL_PRIVATE_PLATFORM_APIS := true

ifeq ($(shell test $(PLATFORM_SDK_VERSION) -ge 26 && echo OK),OK)
LOCAL_PROPRIETARY_MODULE := true
else
LOCAL_PRIVILEGED_MODULE := true
endif

#include frameworks/base/packages/SettingsLib/common.mk
LOCAL_JAVA_LIBRARIES := droidlogic droidlogic-tv
LOCAL_STATIC_ANDROID_LIBRARIES := \
    androidx.legacy_legacy-support-v4 \
    androidx.recyclerview_recyclerview \
    androidx.preference_preference \
    androidx.appcompat_appcompat \
    androidx.legacy_legacy-preference-v14 \
    androidx.leanback_leanback-preference \
    androidx.leanback_leanback

LOCAL_RESOURCE_DIR := $(LOCAL_PATH)/res

LOCAL_SRC_FILES := \
    $(call all-java-files-under, java) \
    $(call all-Iaidl-files-under, java)
#include frameworks/opt/setupwizard/library/common-gingerbread.mk
#include frameworks/base/packages/SettingsLib/common.mk

ifndef PRODUCT_SHIPPING_API_LEVEL
LOCAL_PRIVATE_PLATFORM_APIS := true
endif

FILE := device/hardkernel/odroidn2/files/OdroidSettings/AndroidManifest-common.xml

ifeq ($(FILE), $(wildcard $(FILE)))
LOCAL_FULL_LIBS_MANIFEST_FILES := $(FILE)
LOCAL_JACK_COVERAGE_INCLUDE_FILTER := com.hardkernel.odroid.settings*
endif

LOCAL_JNI_SHARED_LIBRARIES := libsystemcontrol_jni

include $(BUILD_PACKAGE)

include $(call all-makefiles-under,$(LOCAL_PATH))
