
private static Record[] sort(Record[] r)
    {
        Record[] heap = new Record[r.length];
        int s = 0; //heap size
        
        int in = 0; //input index
        
        //make heap array from input records
        for(int x = 0; x < r.length; x++)
            heap[s++] = r[in++];

        MinHeap<Record> mh = new MinHeap<>(r, s, r.length);

        //sort into output array
        Record[] output= new Record[r.length];
        int out = 0;

        Record[] later = new Record[r.length];
        int future = 0;
        
        while(true)
        {
            while (mh.heapSize() > 0)
            {
                Record min = mh.removeMin();
                output[out++] = min;
    
                if (in < r.length)
                {
                    Record rec = r[in++];
                    if(rec.getKey() >= min.getKey())
                        mh.insert(rec);
                    else
                        later[future++] = rec;
                }
            }
    
            if (future > 0) //if has more to sort
            {
                s = future; //update heap to sort next
                for (int i = 0; i < future; i++)
                    heap[i] = later[i];
                future = 0;
                mh = new MinHeap<>(heap, s, r.length);
            }
            else
                break;
        }
        
        return output;

    }
