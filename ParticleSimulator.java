import java.util.*;
import java.util.function.*;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import javax.sound.sampled.*;

public class ParticleSimulator extends JPanel {
	private Heap<Event> _events;
	private java.util.List<Particle> _particles;
	private double _duration;
	private int _width;

	/**
	 * @param filename the name of the file to parse containing the particles
	 */
	public ParticleSimulator (String filename) throws IOException {
		_events = new HeapImpl<>();

		// Parse the specified file and load all the particles.
		Scanner s = new Scanner(new File(filename));
		_width = s.nextInt();
		_duration = s.nextDouble();
		s.nextLine();
		_particles = new ArrayList<>();
		while (s.hasNext()) {
			String line = s.nextLine();
			Particle particle = Particle.build(line);
			_particles.add(particle);
		}

		setPreferredSize(new Dimension(_width, _width));
	}

	@Override
	/**
	 * Draws all the particles on the screen at their current locations
	 * DO NOT MODIFY THIS METHOD
	 */
        public void paintComponent (Graphics g) {
		g.clearRect(0, 0, _width, _width);
		for (Particle p : _particles) {
			p.draw(g);
		}
	}

	// Helper class to signify the final event of the simulation.
	private class TerminationEvent extends Event {
		TerminationEvent (double timeOfEvent) {
			super(timeOfEvent, 0);
		}
	}

	/**
	 * Helper method to update the positions of all the particles based on their current velocities.
	 */
	private void updateAllParticles (double delta) {
		for (Particle p : _particles) {
			p.update(delta);
		}
	}

	/**
	 * Helper to add collision times for each particle p, to be used in the "simulate" method.
	 * @param p Particle to add collision times for
	 * @param time current time of the event
	 */
	private void addP2PTimes (Particle p, double time) {
		for (Particle q : _particles) {
			if (p == null) {
				continue;
			} else if (p != q) {
				if (p.getCollisionTime(q) != Double.POSITIVE_INFINITY) {
					_events.add(new Event(time + p.getCollisionTime(q), time, p, q));
				}
			}
		}
	}

	/**
	 * Helper to add wall collision times for each particle p, to be used in the "simulate" method.
	 * @param p Particle to add collision times for
	 * @param wallSide the side of the wall that the particle is colliding with
	 * @param time current time of the event
	 */
	private void addP2WTimes (Particle p, String wallSide, double time) {
		double collisionTime = p.getWallCollisionTime(wallSide, _width);
		if (collisionTime != Double.POSITIVE_INFINITY) {
			_events.add(new Event(time + collisionTime, time, p, wallSide));
		}
	}

	/**
	 * Call addP2WTimes on all sides of the wall for each particle p.
	 * @param p
	 * @param time
	 */
	private void addAllP2WTimes (Particle p, double time) {
		addP2WTimes(p, "left", time);
		addP2WTimes(p, "right", time);
		addP2WTimes(p, "top", time);
		addP2WTimes(p, "bottom", time);
	}

	/**
	 * Executes the actual simulation.
	 */
	private void simulate (boolean show) {
		double lastTime = 0;

		// Create initial events, i.e., all the possible
		// collisions between all the particles and each other,
		// and all the particles and the walls.

		// add the initial set of possible collisions to the event queue
		for (Particle p : _particles) {
			addAllP2WTimes(p, lastTime); // add all wall collision times for each particle
			addP2PTimes(p, lastTime); // add all particle collision times for each particle
		}
		
		_events.add(new TerminationEvent(_duration));
		while (_events.size() > 0) {
			Event event = _events.removeFirst();
			double delta = event._timeOfEvent - lastTime;

			if (event instanceof TerminationEvent) {
				updateAllParticles(delta);
				break;
			}


			// if event is no longer valid, skip it
			if (event._constructorType.equals("P2P")) {
				if (event._timeEventCreated < event._p.getLastUpdateTime() ||
						event._timeEventCreated < event._q.getLastUpdateTime()) {
					continue;
				}
			} else if (event._constructorType.equals("P2W")) {
				if (event._timeEventCreated < event._p.getLastUpdateTime()) {
					continue;
				}
			}

			// Since the event is valid, then pause the simulation for the right
			// amount of time, and then update the screen.
			if (show) {
				try {
					Thread.sleep((long) (delta * 69)); // slows down the simulation
				} catch (InterruptedException ie) {}
			}

			// Update positions of all particles
			updateAllParticles(delta);

			// Update the velocity of the particle(s) involved in the collision
			// (either for a particle-wall collision or a particle-particle collision).
			// You should call the Particle.updateAfterCollision method at some point.
			if (event._constructorType.equals("P2P")) {
				event._p.updateAfterCollision(event._timeOfEvent, event._q); //
				addP2PTimes(event._p, event._timeOfEvent);
				addAllP2WTimes(event._p, event._timeOfEvent);
				addP2PTimes(event._q, event._timeOfEvent);
				addAllP2WTimes(event._q, event._timeOfEvent);
			} else { // P2W
				event._p.updateAfterWallCollision(event._wallSide, event._timeOfEvent);
				addP2PTimes(event._p, event._timeOfEvent);
				addAllP2WTimes(event._p, event._timeOfEvent);
			}

			// Update the time of our simulation
			lastTime = event._timeOfEvent;

			// Redraw the screen
			if (show) {
				repaint();
			}
		}

		// Print out the final state of the simulation
		System.out.println(_width);
		System.out.println(_duration);
		for (Particle p : _particles) {
			System.out.println(p);
		}
	}

	public static void main (String[] args) throws IOException {
		if (args.length < 1) {
			System.out.println("Usage: java ParticleSimulator <filename>");
			System.exit(1);
		}

		ParticleSimulator simulator;

		simulator = new ParticleSimulator(args[0]);
		JFrame frame = new JFrame();
		frame.setTitle("Particle Simulator");
		frame.getContentPane().setLayout(new BorderLayout());
		frame.getContentPane().add(simulator, BorderLayout.CENTER);
		frame.setVisible(true);
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		simulator.simulate(true);
	}
}
