package ass01.jpf;
import ass01.jpf.barrier.Barrier;

import java.util.List;

public class UpdateBoids extends Thread {
    private List<Boid> boids;
    private BoidsModel model;
    private Barrier barrierVel;
    private Barrier barrierSync;

    public UpdateBoids(List<Boid> boids, BoidsModel model, Barrier barrierVel, Barrier barrierSync) {
        this.boids = boids;
        this.model = model;
        this.barrierVel = barrierVel;
        this.barrierSync = barrierSync;
    }

    @Override
    public void run() {
        while (!this.isInterrupted()) {
            try {
                barrierSync.hitAndWaitAll();
            } catch (InterruptedException e) {
                break;
            }
//            for (Boid boid : boids) {
//                boid.updateVelocity(model);
//            }
            try {
                barrierVel.hitAndWaitAll();
            } catch (InterruptedException e) {
                break;
            }
//            for (Boid boid : boids) {
//                boid.updatePos(model);
//            }
            try {
                barrierSync.hitAndWaitAll();
            } catch (InterruptedException e) {
                break;
            }
        }
    }

}