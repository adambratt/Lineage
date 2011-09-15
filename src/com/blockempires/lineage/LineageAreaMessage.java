package com.blockempires.lineage;

public class LineageAreaMessage {
	public String msg;
	public int time;
	
	public LineageAreaMessage(String msg, int time) {
		// TODO Auto-generated constructor stub
		this.msg=msg;
		this.time=time;
	}
	
	public String getMsg(){
		return msg;
	}
	
	public int getTime(){
		return (time > 0) ? time : 0;
	}

	public void setDuration(int ticks) {
		this.time=ticks;		
	}
}
