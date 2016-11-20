package com.carlschroedl.gephi.spanningtree;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.gephi.graph.api.Edge;
import org.gephi.graph.api.EdgeIterable;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.Node;
import org.gephi.graph.impl.utils.MapDeepEquals;
import org.openide.util.Pair;

/**
 *
 * @author carlschroedl
 */
public class GraphTopologyEquals {

    private static Pair getSourceTargetPair(Edge e) {
        Object aSourceId = e.getSource().getId();
        Object aTargetId = e.getTarget().getId();
        Pair<Object, Object> pair = Pair.of(aSourceId, aTargetId);
        return pair;

    }
    /**
     * Compares edge collections based on source node id , target node id, and edge weight.
     * @param aEdgeCollection
     * @param bEdgeCollection
     * @return true if all edges in both collections match, otherwise false.
     */
    public static boolean edgeCollectionsAreEqual(Collection<Edge> aEdgeCollection, Collection<Edge> bEdgeCollection) {
        boolean equal = true;
        if (aEdgeCollection.size() == bEdgeCollection.size()) {
            Map<Pair<Object, Object>, Double> aEdgeMap = new HashMap<Pair<Object, Object>, Double>();

            for (Edge aEdge : aEdgeCollection) {
                double weight = aEdge.getWeight();
                aEdgeMap.put(getSourceTargetPair(aEdge), weight);
            }

            Map<Pair<Object, Object>, Double> bEdgeMap = new HashMap<Pair<Object, Object>, Double>();
            for (Edge aEdge : aEdgeCollection) {
                double weight = aEdge.getWeight();
                bEdgeMap.put(getSourceTargetPair(aEdge), weight);
            }

            equal = MapDeepEquals.mapDeepEquals(aEdgeMap, bEdgeMap);
        } else {
            equal = false;
        }
        return equal;
    }

    public static boolean graphsHaveSameTopology(Graph a, Graph b) {
        boolean equal = true;
        a.writeLock();
        b.writeLock();
        try {
            if (a.getNodeCount() == b.getNodeCount() && a.getEdgeCount() == b.getEdgeCount()) {
                for (Node aNode : a.getNodes()) {
                    Node bNode = b.getNode(aNode.getId());
                    if (null == bNode) {
                        equal = false;
                        break;
                    } else if (!edgeCollectionsAreEqual(a.getEdges(aNode).toCollection(), b.getEdges(aNode).toCollection())) {
                        equal = false;
                        break;
                    }
                }
            } else {
                equal = false;
            }
        } finally {
            a.writeUnlock();
            b.writeUnlock();
        }
        return equal;
    }

}
