package ass01.jpf;
import ass01.jpf.barrier.Barrier;

import java.util.List;

public class UpdateBoids extends Thread {
    private List<Boid> boids;
    private BoidsModel model;
    private Barrier barrierVel;
    private Barrier barrierSync;
//    private boolean isRunning;

    public UpdateBoids(List<Boid> boids, BoidsModel model, Barrier barrierVel, Barrier barrierSync) {
        this.boids = boids;
        this.model = model;
        this.barrierVel = barrierVel;
        this.barrierSync = barrierSync;
//        this.isRunning = true;
    }

//    @Override
//    public void interrupt() {
//        super.interrupt();
//        this.isRunning = false;
//    }

    @Override
    public void run() {
        while (!this.isInterrupted()) {
            if(this.isInterrupted()) break;
            try {
                barrierSync.hitAndWaitAll();
            } catch (InterruptedException e) {
                break;
            }
//            for (Boid boid : boids) {
//                boid.updateVelocity(model);
//            }
            if(this.isInterrupted()) break;
            try {
                barrierVel.hitAndWaitAll();
            } catch (InterruptedException e) {
                break;
            }
//            for (Boid boid : boids) {
//                boid.updatePos(model);
//            }
            if(this.isInterrupted()) break;
            try {
                barrierSync.hitAndWaitAll();
            } catch (InterruptedException e) {
                break;
            }
        }
    }

}