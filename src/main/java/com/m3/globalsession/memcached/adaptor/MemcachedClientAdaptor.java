/*
 * Copyright 2012 M3, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package com.m3.globalsession.memcached.adaptor;

import com.m3.globalsession.memcached.impl.ClientImpl;

public interface MemcachedClientAdaptor {

    public static final String Spymemcached = "com.m3.globalsession.memcached.adaptor.SpymemcachedAdaptor";

    public static final String Xmemcached = "com.m3.globalsession.memcached.adaptor.XmemcachedAdaptor";

    Class<? extends ClientImpl> getClientImplClass();

}

