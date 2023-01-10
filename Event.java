/**
 * Represents a collision between a particle and another particle, or a particle and a wall.
 */
public class Event implements Comparable<Event> {
	double _timeOfEvent;
	double _timeEventCreated;
	Particle _p;
	Particle _q;
	String _wallSide;
	String _constructorType;
	/**
	 * @param timeOfEvent the time when the collision will take place
	 * @param timeEventCreated the time when the event was first instantiated and added to the queue
	 */
	// PARTICLE-TO-PARTICLE COLLISION
	public Event (double timeOfEvent, double timeEventCreated, Particle p, Particle q) {
		_timeOfEvent = timeOfEvent;
		_timeEventCreated = timeEventCreated;
		_p = p;
		_q = q;
		_constructorType = "P2P";
	}
	// PARTICLE-TO-WALL COLLISION
	public Event (double timeOfEvent, double timeEventCreated, Particle p, String wallSide) {
		_timeOfEvent = timeOfEvent;
		_timeEventCreated = timeEventCreated;
		_p = p;
		_wallSide = wallSide;
		_constructorType = "P2W";
	}

	// DEFAULT CONSTRUCTOR
	public Event (double timeOfEvent, double timeEventCreated) {
		_timeOfEvent = timeOfEvent;
		_timeEventCreated = timeEventCreated;
	}

	@Override
	/**
	 * Compares two Events based on their event times. Since you are implementing a maximum heap,
	 * this method assumes that the event with the smaller event time should receive higher priority.
	 */
	public int compareTo (Event e) {
		if (_timeOfEvent < e._timeOfEvent) {
			return +1;
		} else if (_timeOfEvent == e._timeOfEvent) {
			return 0;
		} else {
			return -1;
		}
	}
}
