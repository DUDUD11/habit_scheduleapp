package com.example.ticktick2.dataobject;

public class Alarm {

    private String alarmName;  // 알람 이름
    private long alarmTime;    // 알람 시간 (밀리초 단위)
    private int drawId;        // 아이콘 리소스 ID (drawable ID)
    private int frequency;
    private int weekends;      // 주말 설정 (이진 비트 필드로, 예: 1111111이면 매일 알람, 0100000이면 토요일만 알람)

    // 생성자
    public Alarm(String alarmName, long alarmTime, int drawId, int frequency, int weekends) {
        this.alarmName = alarmName;
        this.alarmTime = alarmTime;
        this.drawId = drawId;
        this.frequency = frequency;
        this.weekends = weekends;
    }

    // Getter and Setter Methods
    public String getAlarmName() {
        return alarmName;
    }

    public void setAlarmName(String alarmName) {
        this.alarmName = alarmName;
    }

    public long getAlarmTime() {
        return alarmTime;
    }

    public void setAlarmTime(long alarmTime) {
        this.alarmTime = alarmTime;
    }

    public int getDrawId() {
        return drawId;
    }

    public void setDrawId(int drawId) {
        this.drawId = drawId;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public int getWeekends() {
        return weekends;
    }

    public void setWeekends(int weekends) {
        this.weekends = weekends;
    }

    // toString() 메서드를 추가하면 객체 내용을 쉽게 출력할 수 있습니다.
    @Override
    public String toString() {
        return "Alarm{" +
                "alarmName='" + alarmName + '\'' +
                ", alarmTime=" + alarmTime +
                ", drawId=" + drawId +
                ", frequency=" + frequency +
                ", weekends=" + weekends +
                '}';
    }
}
