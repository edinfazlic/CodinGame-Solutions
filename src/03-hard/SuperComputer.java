import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

class Solution {

	public static void main(String args[]) {
		Scanner in = new Scanner(System.in);
		int N = in.nextInt();

		List<Schedule> scheduleTimes = new ArrayList<Schedule>();
		for (int i = 0; i < N; i++) {
			int J = in.nextInt();
			int D = in.nextInt();
			scheduleTimes.add(new Schedule(J, J + D - 1));
		}
		Collections.sort(scheduleTimes);

		int calculations = 0, lastEndTime = 0;
		for(Schedule schedule : scheduleTimes) {
			if(schedule.endTime > lastEndTime && schedule.startTime > lastEndTime) {
				lastEndTime = schedule.endTime;
				calculations++;
			}
		}

		System.out.println(calculations);
	}

	static class Schedule implements Comparable<Schedule> {
		private int startTime;
		private int endTime;

		public Schedule(int startTime, int endTime) {
			this.startTime = startTime;
			this.endTime = endTime;
		}

		@Override
		public int compareTo(Schedule that) {
			return endTime < that.endTime ? - 1 : endTime > that.endTime ? 1 : 0;
		}
	}
}