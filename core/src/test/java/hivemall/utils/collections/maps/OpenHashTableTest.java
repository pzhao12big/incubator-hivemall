/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package hivemall.utils.collections.maps;

import hivemall.utils.collections.IMapIterator;
import hivemall.utils.collections.maps.OpenHashTable;
import hivemall.utils.lang.ObjectUtils;
import hivemall.utils.lang.mutable.MutableInt;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

public class OpenHashTableTest {

    @Test
    public void testPutAndGet() {
        OpenHashTable<Object, Object> map = new OpenHashTable<Object, Object>(16384);
        final int numEntries = 5000000;
        for (int i = 0; i < numEntries; i++) {
            map.put(Integer.toString(i), i);
        }
        Assert.assertEquals(numEntries, map.size());
        for (int i = 0; i < numEntries; i++) {
            Object v = map.get(Integer.toString(i));
            Assert.assertEquals(i, v);
        }
        map.put(Integer.toString(1), Integer.MAX_VALUE);
        Assert.assertEquals(Integer.MAX_VALUE, map.get(Integer.toString(1)));
        Assert.assertEquals(numEntries, map.size());
    }

    @Test
    public void testIterator() {
        OpenHashTable<String, Integer> map = new OpenHashTable<String, Integer>(1000);
        IMapIterator<String, Integer> itor = map.entries();
        Assert.assertFalse(itor.hasNext());

        final int numEntries = 1000000;
        for (int i = 0; i < numEntries; i++) {
            map.put(Integer.toString(i), i);
        }

        itor = map.entries();
        Assert.assertTrue(itor.hasNext());
        while (itor.hasNext()) {
            Assert.assertFalse(itor.next() == -1);
            String k = itor.getKey();
            Integer v = itor.getValue();
            Assert.assertEquals(Integer.valueOf(k), v);
        }
        Assert.assertEquals(-1, itor.next());
    }

    @Test
    public void testIteratorGetProbe() {
        OpenHashTable<String, MutableInt> map = new OpenHashTable<String, MutableInt>(100);
        IMapIterator<String, MutableInt> itor = map.entries();
        Assert.assertFalse(itor.hasNext());

        final int numEntries = 1000000;
        for (int i = 0; i < numEntries; i++) {
            map.put(Integer.toString(i), new MutableInt(i));
        }

        final MutableInt probe = new MutableInt();
        itor = map.entries();
        Assert.assertTrue(itor.hasNext());
        while (itor.hasNext()) {
            Assert.assertFalse(itor.next() == -1);
            String k = itor.getKey();
            itor.getValue(probe);
            Assert.assertEquals(Integer.valueOf(k).intValue(), probe.intValue());
        }
        Assert.assertEquals(-1, itor.next());
    }

    @Test
    public void testSerDe() throws IOException, ClassNotFoundException {
        OpenHashTable<Object, Object> map = new OpenHashTable<Object, Object>(16384);
        final int numEntries = 100000;
        for (int i = 0; i < numEntries; i++) {
            map.put(Integer.toString(i), i);
        }

        byte[] serialized = ObjectUtils.toBytes(map);
        map = new OpenHashTable<Object, Object>();
        ObjectUtils.readObject(serialized, map);

        Assert.assertEquals(numEntries, map.size());
        for (int i = 0; i < numEntries; i++) {
            Object v = map.get(Integer.toString(i));
            Assert.assertEquals(i, v);
        }
        map.put(Integer.toString(1), Integer.MAX_VALUE);
        Assert.assertEquals(Integer.MAX_VALUE, map.get(Integer.toString(1)));
        Assert.assertEquals(numEntries, map.size());
    }


    @Test
    public void testCompressedSerDe() throws IOException, ClassNotFoundException {
        OpenHashTable<Object, Object> map = new OpenHashTable<Object, Object>(16384);
        final int numEntries = 100000;
        for (int i = 0; i < numEntries; i++) {
            map.put(Integer.toString(i), i);
        }

        byte[] serialized = ObjectUtils.toCompressedBytes(map);
        map = new OpenHashTable<Object, Object>();
        ObjectUtils.readCompressedObject(serialized, map);

        Assert.assertEquals(numEntries, map.size());
        for (int i = 0; i < numEntries; i++) {
            Object v = map.get(Integer.toString(i));
            Assert.assertEquals(i, v);
        }
        map.put(Integer.toString(1), Integer.MAX_VALUE);
        Assert.assertEquals(Integer.MAX_VALUE, map.get(Integer.toString(1)));
        Assert.assertEquals(numEntries, map.size());
    }

}
