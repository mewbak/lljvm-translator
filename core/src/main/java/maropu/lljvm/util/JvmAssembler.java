/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package maropu.lljvm.util;

import java.io.*;
import java.nio.charset.StandardCharsets;

import jasmin.ClassFile;

import maropu.lljvm.LLJVMLoader;
import maropu.lljvm.LLJVMNative;
import maropu.lljvm.LLJVMRuntimeException;

public class JvmAssembler {

  public static final String LLJVM_GENERATED_CLASSNAME;

  static {
    String magicNumber = null;
    try {
      final LLJVMNative lljvmApi = LLJVMLoader.loadLLJVMApi();
      magicNumber = lljvmApi.magicNumber();
    } catch(LLJVMRuntimeException e) {
      throw e; // Just rethrow the exception
    }
    LLJVM_GENERATED_CLASSNAME = String.format("GeneratedClass%s", magicNumber);
  }

  public static byte[] compile(String code) {
    return compile(code.getBytes(StandardCharsets.UTF_8));
  }

  public static byte[] compile(byte[] code) {
    ClassFile classFile = new ClassFile();
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    try (Reader in = new InputStreamReader(new ByteArrayInputStream(code))) {
      classFile.readJasmin(in, LLJVM_GENERATED_CLASSNAME, false);
      classFile.write(out);
    } catch (Exception e) {
      throw new LLJVMRuntimeException(e.getMessage());
    }
    return out.toByteArray();
  }
}