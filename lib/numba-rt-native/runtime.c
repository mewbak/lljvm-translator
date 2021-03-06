/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.io/github/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

#include "io_github_maropu_lljvm_runtime_NumbaRuntimeNative.h"

#include <Python.h>

// TODO: Needs to check all the exported functions/data in Numba
#define NUMBA_EXPORT_FUNC(_rettype) static _rettype
#define NUMBA_EXPORT_DATA(_vartype) static _vartype

#include "numba/numba/_helperlib.c"

JNIEXPORT void JNICALL Java_io_github_maropu_lljvm_runtime_NumbaRuntimeNative_initialize
    (JNIEnv *env, jobject self) {
  Py_Initialize();
}

JNIEXPORT void JNICALL Java_io_github_maropu_lljvm_runtime_NumbaRuntimeNative_setSystemPath
    (JNIEnv *env, jobject self, jstring path) {
  PyObject *sys_path = PySys_GetObject("path");
  const char *p = (*env)->GetStringUTFChars(env, path, NULL);
  PyList_Insert(sys_path, 0, PyString_FromString(p));
  (*env)->ReleaseStringUTFChars(env, path, p);
}

JNIEXPORT jint JNICALL Java_io_github_maropu_lljvm_runtime_NumbaRuntimeNative__1numba_1attempt_1nocopy_1reshape
    (JNIEnv *env, jobject self, jlong nd, jlong dims, jlong strides, jlong newnd, jlong newdims, jlong newstrides, jlong itemsize, jint is_f_order) {
  return (jint) numba_attempt_nocopy_reshape(
    (npy_intp) nd,
    (const npy_intp *) dims,
    (const npy_intp *) strides,
    (npy_intp) newnd,
    (const npy_intp *) newdims,
    (npy_intp *) newstrides,
    (npy_intp) itemsize,
    (int) is_f_order);
}

JNIEXPORT jlong JNICALL Java_io_github_maropu_lljvm_runtime_NumbaRuntimeNative_numba_1get_1np_1random_1state
    (JNIEnv *env, jobject self) {
  return (jlong) numba_get_np_random_state();
}

JNIEXPORT void JNICALL Java_io_github_maropu_lljvm_runtime_NumbaRuntimeNative_numba_1rnd_1shuffle
    (JNIEnv *env, jobject self, jlong state) {
  numba_rnd_shuffle((rnd_state_t *) state);
}

JNIEXPORT jint JNICALL Java_io_github_maropu_lljvm_runtime_NumbaRuntimeNative_numba_1xxdot
    (JNIEnv *env, jobject self, jbyte kind, jbyte conjugate, jlong n, jlong x, jlong y, jlong result) {
  return (jint) numba_xxdot(
    (char) kind,
    (char) conjugate,
    (Py_ssize_t) n,
    (void *) x,
    (void *) y,
    (void *) result);
}

JNIEXPORT jint JNICALL Java_io_github_maropu_lljvm_runtime_NumbaRuntimeNative_numba_1xxgemv
    (JNIEnv *env, jobject self, jbyte kind, jbyte trans, jlong m, jlong n, jlong alpha, jlong a, jlong lda, jlong x, jlong beta, jlong y) {
  return (jint) numba_xxgemv(
    (char) kind,
    (char) trans,
    (Py_ssize_t) m,
    (Py_ssize_t) n,
    (void *) alpha,
    (void *) a,
    (Py_ssize_t) lda,
    (void *) x,
    (void *) beta,
    (void*) y);
}

JNIEXPORT jint JNICALL Java_io_github_maropu_lljvm_runtime_NumbaRuntimeNative_numba_1xxgemm
    (JNIEnv *env, jobject self, jbyte kind, jbyte transa, jbyte transb, jlong m, jlong n, jlong k, jlong alpha, jlong a, jlong lda, jlong b, jlong ldb, jlong beta, jlong c, jlong ldc) {
  return (jint) numba_xxgemm(
    (char) kind,
    (char) transa,
    (char) transb,
    (Py_ssize_t) m,
    (Py_ssize_t) n,
    (Py_ssize_t) k,
    (void *) alpha,
    (void *) a,
    (Py_ssize_t) lda,
    (void *) b,
    (Py_ssize_t) ldb,
    (void *) beta,
    (void*) c,
    (Py_ssize_t) ldc);
}

