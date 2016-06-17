/*
 * Copyright 2016 MiLaboratory.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.milaboratory.core.io.sequence.fasta;

import org.junit.Assert;
import org.junit.Test;

public class RandomAccessFastaIndexTest {
    @Test(expected = IllegalStateException.class)
    public void test1() throws Exception {
        RandomAccessFastaIndex.IndexBuilder builder = new RandomAccessFastaIndex.IndexBuilder(1024);
        builder.setLastRecordLength(12);
    }

    @Test(expected = IllegalStateException.class)
    public void test2() throws Exception {
        RandomAccessFastaIndex.IndexBuilder builder = new RandomAccessFastaIndex.IndexBuilder(1024);
        builder.addRecord("Record1", 343L);
        builder.addRecord("Record2", 3445L);
    }

    @Test
    public void test3() throws Exception {
        RandomAccessFastaIndex.IndexBuilder builder = new RandomAccessFastaIndex.IndexBuilder(1024);
        builder.addRecord("Record1", 13L);
        builder.addIndexPoint(13L + 1024 + 10);
        builder.setLastRecordLength(1700L);

        builder.addRecord("Record2", 2013L);
        builder.addIndexPoint(2013L + 1024 + 13);
        builder.addIndexPoint(2013L + 1024 * 2 + 24);
        builder.setLastRecordLength(2700L);

        RandomAccessFastaIndex index = builder.build();

        Assert.assertEquals("Record1", index.getRecordByIndex(0).getDescription());
        Assert.assertEquals("Record2", index.getRecordByIndex(1).getDescription());

        Assert.assertEquals(1700, index.getRecordByIndex(0).getLength());
        Assert.assertEquals(2700, index.getRecordByIndex(1).getLength());

        Assert.assertEquals((13L + 1024 + 10) << RandomAccessFastaIndex.FILE_POSITION_OFFSET, index.getRecordByIndex(0).queryPosition(1024));
        Assert.assertEquals(((13L) << RandomAccessFastaIndex.FILE_POSITION_OFFSET) | 1023, index.getRecordByIndex(0).queryPosition(1023));
        Assert.assertEquals(((13L + 1024 + 10) << RandomAccessFastaIndex.FILE_POSITION_OFFSET) | 12, index.getRecordByIndex(0).queryPosition(1024 + 12));

        Assert.assertEquals((2013L + 1024 + 13) << RandomAccessFastaIndex.FILE_POSITION_OFFSET, index.getRecordByIndex(1).queryPosition(1024));
        Assert.assertEquals(((2013L + 1024 + 13) << RandomAccessFastaIndex.FILE_POSITION_OFFSET) | 6, index.getRecordByIndex(1).queryPosition(1030));
        Assert.assertEquals(((2013L + 1024 * 2 + 24) << RandomAccessFastaIndex.FILE_POSITION_OFFSET), index.getRecordByIndex(1).queryPosition(2048));
    }
}