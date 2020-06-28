
public class ProducerConsumer {

    public static void main(final String[] args) {
        ProducerConsumer.Buffer buffer = new ProducerConsumer.Buffer();
        new Thread(new ProducerConsumer.Producer(buffer)).start();
        new Thread(new ProducerConsumer.Consumer(buffer)).start();
    }

    public static class Buffer {
        private int itemCount = 0;
        // 缓冲区大小
        public static int BUFFER_SIZE = 10;

        void pushBuffer() {
            this.itemCount++;
        }

        void popBuffer() {
            this.itemCount--;
        }

        public int itemCount() {
            return itemCount;
        }
    }

    /**
     * 生产者
     */
    public static class Producer implements Runnable {

        private Buffer buffer;

        Producer(Buffer buffer) {
            this.buffer = buffer;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep((int) (Math.random() * 1000) + 500);
                } catch (final Exception e) {
                    e.printStackTrace();
                }

                synchronized (buffer) {
                    while (buffer.itemCount() == Buffer.BUFFER_SIZE) {
                        try {
                            System.out.println("生产池已满.等待消费者消费");
                            buffer.wait();
                        } catch (final InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    buffer.pushBuffer();
                    System.out.println("生产者 生产 目前有: " + buffer.itemCount() + "个商品");
                    buffer.notifyAll();
                }
            }
        }

    }

    /**
     * 消费者
     */
    public static class Consumer implements Runnable {

        private Buffer buffer;

        Consumer(Buffer buffer) {
            this.buffer = buffer;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep((int) (Math.random() * 3000) + 500);
                } catch (final Exception e) {
                    e.printStackTrace();
                }

                synchronized (buffer) {
                    while (buffer.itemCount() == 0) {
                        try {
                            System.out.println("生产池空了.等待生产者生产");
                            buffer.wait();
                        } catch (final InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    buffer.popBuffer();
                    System.out.println("消费者 消费 目前有: " + buffer.itemCount() + "个商品");
                    buffer.notify();
                }
            }
        }
    }
}