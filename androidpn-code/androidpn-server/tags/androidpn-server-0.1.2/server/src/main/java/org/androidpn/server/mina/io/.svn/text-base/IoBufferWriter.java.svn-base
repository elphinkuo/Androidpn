/*
 * Copyright 2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.androidpn.server.mina.io;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.CharsetEncoder;

import org.apache.mina.core.buffer.IoBuffer;

/** 
 * Class desciption here.
 *
 * @author Sehwan Noh (sehnoh@gmail.com)
 */
public class IoBufferWriter extends Writer {

    private CharsetEncoder encoder;

    private IoBuffer ioBuffer;

    public IoBufferWriter(IoBuffer ioBuffer, CharsetEncoder encoder) {
        this.encoder = encoder;
        this.ioBuffer = ioBuffer;
    }

    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {
        ioBuffer.putString(new String(cbuf, off, len), encoder);
    }

    @Override
    public void flush() throws IOException {
        // Ignore
    }

    @Override
    public void close() throws IOException {
        // Ignore
    }

}
