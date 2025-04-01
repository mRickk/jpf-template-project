package ass01.jpf;

import ass01.jpf.barrier.Barrier;
import ass01.jpf.barrier.CyclicBarrierImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BoidsSimulator {

    private BoidsModel model;
    private Optional<BoidsView> view;
//    private static final int FRAMERATE = 25;
//    private int framerate;
    private Barrier barrierVel, barrierSync;
    private final List<UpdateBoids> updateBoidsList = new ArrayList<>();
    private final int nCycle;

    private final Lock lock = new ReentrantLock();
    private final Condition cond = lock.newCondition();
    private boolean isSimulationRunning = false;
    
    public BoidsSimulator(BoidsModel model, int nCycle) {
        this.model = model;
        view = Optional.empty();
        this.nCycle = nCycle;
    }

    public void attachView(BoidsView view) {
        this.view = Optional.of(view);
    }

    public void startSimulator() {
        try {
            lock.lock();
            isSimulationRunning = true;
            cond.signalAll();
        }
        finally {
            lock.unlock();
        }
    }

    public void stopSimulator() {
        try {
            lock.lock();
            isSimulationRunning = false;
            cond.signalAll();
        }
        finally {
            lock.unlock();
        }
    }

    public void runSimulation() {
        var boids = model.getBoids();
        var nboids = boids.size();
        int nthread = Runtime.getRuntime().availableProcessors() + 1;
        int div_factor = nboids / nthread;

        this.barrierVel = new CyclicBarrierImpl(nthread);
        this.barrierSync = new CyclicBarrierImpl(nthread + 1);

        updateBoidsList.clear();
        for (int i = 0; i < nthread; i++) {
            var subList = boids.subList(i * div_factor, Math.min((i + 1) * div_factor, boids.size()));
            var ub = new UpdateBoids(subList, model, barrierVel, barrierSync);
            updateBoidsList.add(ub);
        }
        updateBoidsList.forEach(UpdateBoids::start);

        for (int i = 0; i < nCycle; i++) {
            try {
                lock.lock();
                while(!isSimulationRunning) {
                    try {
                        cond.await();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            } finally {
                lock.unlock();
            }

//            var t0 = System.currentTimeMillis();

            try {
                barrierSync.hitAndWaitAll();//Last, breaking barrier
                barrierSync.hitAndWaitAll();//First, wait
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

//            if (view.isPresent()) {
//                view.get().update(framerate);
//                var t1 = System.currentTimeMillis();
//                var dtElapsed = t1 - t0;
//                var frameratePeriod = 1000/FRAMERATE;
//
//                if (dtElapsed < frameratePeriod) {
//                    try {
//                        Thread.sleep(frameratePeriod - dtElapsed);
//                    } catch (Exception ex) {}
//                    framerate = FRAMERATE;
//                } else {
//                    framerate = (int) (1000/dtElapsed);
//                }
//            }

        }
    }
}
