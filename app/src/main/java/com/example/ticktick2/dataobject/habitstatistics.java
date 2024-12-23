package com.example.ticktick2.dataobject;

public class habitstatistics {

        public int monthlycheckin;
    public int wholecheckin;
    public float monthlyratio;
    public int consecutive;

        public habitstatistics(int _monthlycheckin,int _wholecheckin,float _monthlyratio,int _consecutive)
        {
            monthlycheckin=_monthlycheckin;
            wholecheckin=_wholecheckin;
            monthlyratio=_monthlyratio;
            consecutive=_consecutive;

        }

    public habitstatistics()
    {}

}
