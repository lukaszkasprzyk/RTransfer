package com.charfrequency;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ThreadSafeArrayList<E> {
	final Lock lock = new ReentrantLock();

	private final List<E> list = new ArrayList<E>();

	private static int i = 0;

	public void set(E o) {
		

		try {
			i++;
			list.add(o);
			System.out.println("Adding element by thread" + Thread.currentThread().getName());
		} finally {
			// lock.unlock();
		}
	}

	public static void main(String[] args) {
		String k = "PPIE".intern();
		final ThreadSafeArrayList<String> lockExample = new ThreadSafeArrayList<String>();
		Runnable syncThread = new Runnable() {

			@Override
			public void run() {
				lockExample.lock.lock();
				while (i < 6) {
					lockExample.set(String.valueOf(i));

					try {
						Thread.sleep(510);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				lockExample.lock.unlock();
			}
		};
		Runnable lockingThread = new Runnable() {

			@Override
			public void run() {
				lockExample.lock.lock();
				while (i < 16) {
					lockExample.set(String.valueOf(i));
					try {
						Thread.sleep(510);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				lockExample.lock.unlock();
			}
		};

		Thread t1 = new Thread(syncThread, "syncThread");
		Thread t2 = new Thread(lockingThread, "lockingThread");
		t1.start();
		t2.start();
	}
}
