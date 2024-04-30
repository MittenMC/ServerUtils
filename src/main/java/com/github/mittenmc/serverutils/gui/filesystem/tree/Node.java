package com.github.mittenmc.serverutils.gui.filesystem.tree;

import com.github.mittenmc.serverutils.gui.pages.ItemGenerator;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * A generic node class.
 * @author GavvyDizzle
 * @version 1.0.10
 * @since 1.0.10
 */
@SuppressWarnings("unused")
public abstract class Node implements Comparable<Node>, ItemGenerator {

    @Getter private final String name;
    private final @Nullable Node parent;
    private final List<Node> children;

    public Node(@Nullable Node parent, String name) {
        this.parent = parent;
        this.name = name;
        children = new ArrayList<>();
    }

    /**
     * Defines the sorting weight of this node.
     * Higher weights will sort later.
     * @return The weight of this node type.
     */
    public abstract int getSortWeight();

    /**
     * Adds a new child to this node.
     * @param node The child node
     */
    public void add(Node node) {
        children.add(node);
        Collections.sort(children);
    }

    /**
     * Attempts to find a matching node in this tree.
     * @param node The node to search for
     * @return The node with the matching name and weight or null
     */
    @Nullable
    public Node find(@NotNull Node node) {
        Queue<Node> queue = new ArrayDeque<>();
        queue.add(this);

        while (!queue.isEmpty()) {
            Node currentNode = queue.poll();
            if (node.equals(currentNode)) {
                return currentNode;
            }
            queue.addAll(currentNode.getChildren());
        }
        return null;
    }

    /**
     * @return If this node has no children
     */
    public boolean isLeaf() {
        return children.isEmpty();
    }

    /**
     * @return The root of this tree
     */
    @NotNull
    public Node getRoot() {
        if (parent == null) return this;
        return parent.getRoot();
    }

    /**
     * @return The parent node or null
     */
    @Nullable
    public Node getParent() {
        return parent;
    }

    /**
     * @return A sorted list of child nodes
     */
    @NotNull
    public List<Node> getChildren() {
        return children;
    }

    @Override
    public int compareTo(@NotNull Node o) {
        if (getSortWeight() != o.getSortWeight()) {
            return Integer.compare(getSortWeight(), o.getSortWeight());
        }
        return name.compareTo(o.name);
    }

    public boolean equals(@NotNull Node o) {
        return getSortWeight() == o.getSortWeight() && name.equals(o.name);
    }
}