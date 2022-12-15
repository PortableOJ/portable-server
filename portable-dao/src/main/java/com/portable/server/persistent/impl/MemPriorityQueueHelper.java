package com.portable.server.persistent.impl;

import java.util.PriorityQueue;

import com.portable.server.persistent.PriorityQueueHelper;

/**
 * @author shiroha
 */
public class MemPriorityQueueHelper<E extends Comparable<E>> extends PriorityQueue<E> implements PriorityQueueHelper<E> {
}
