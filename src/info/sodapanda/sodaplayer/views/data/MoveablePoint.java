package info.sodapanda.sodaplayer.views.data;

public class MoveablePoint {
	GiftPoint start_point;
	GiftPoint end_point;

	public MoveablePoint(GiftPoint start_point, GiftPoint end_point) {
		this.start_point = start_point;
		this.end_point = end_point;
	}

	public GiftPoint getStart_point() {
		return start_point;
	}

	public GiftPoint getEnd_point() {
		return end_point;
	}
}
