package ch.heig.gre.groupA;

import ch.heig.gre.Keys;
import ch.heig.gre.graph.Graph;
import ch.heig.gre.graph.GridGraph2D;
import ch.heig.gre.graph.PositiveWeightFunction;
import ch.heig.gre.graph.VertexLabelling;
import ch.heig.gre.maze.MazeSolver;
import ch.heig.gre.maze.Metadata;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;

public final class BfsSolver implements MazeSolver {
    @Override
    public Result solve(GridGraph2D grid, PositiveWeightFunction weights, int source, int destination, VertexLabelling<Integer> distances) {
        // Ne pas modifier
        return solve((Graph) grid, source, destination, distances);
    }

    private class BfsResult { // classe interne pour le retour de la méthode BFS
        int[] distanceToSource;
        int[] parent;
        int[] optimalPaths;

        BfsResult(int[] distanceToSource, int[] parent, int[] optimalPaths) {
            this.distanceToSource = distanceToSource;
            this.parent = parent;
            this.optimalPaths = optimalPaths;
        }
    }

    public Result solve(Graph graph, int source, int destination, VertexLabelling<Integer> distances) {
        BfsResult result = BFS(graph, source, destination, distances);                                              //calcul des distances, parents et nombre de chemins optimaux

        Metadata metadata = new Metadata();                                                                         // stockage des métadonnées
        metadata.put(Keys.LENGTH, result.distanceToSource[destination]);
        metadata.put(Keys.NB_OPTIMAL_PATHS, (long) result.optimalPaths[destination]);


        if (result.parent[destination] == -1) return new Result(new ArrayList<Integer>(), metadata);                   // cas ou aucun chemin a été trouvé


        //preparation du chemin à retourner
        ArrayList<Integer> path = new ArrayList<>(Collections.nCopies(result.distanceToSource[destination] + 1, null));      // declaration du chemin à retourner dans un tableau avec la bonne taille
        path.set(result.distanceToSource[destination],destination);                                                                // on met la destination à la fin du chemin
        int i = 1;                                                                                                  // index pour savoir ou inserer les sommets
        int to = result.parent[destination];                                                                             // on prend le parent de la destination
        while(to != source){                                                                                        // tant que le parent n'est pas la source on l'ajoute a la fin de la liste
            path.set(result.distanceToSource[destination] - i, to);
            to = result.parent[to];
            i++;
        }
        path.set(0,source);                                                                                         // on ajoute la source au début du chemin

        return new Result(path,metadata);
    }

    private BfsResult BFS(Graph graph, int source, int destination, VertexLabelling<Integer> distances) {
        int[] distanceToSource = new int[graph.nbVertices()];          //distance avec le sommet source, -1 si non encore trouvé
        int[] parent = new int[graph.nbVertices()];                    //parant du sommet (celui qui a permis de le découvrir), null si non encore trouvé
        int[] optimalPaths = new int[graph.nbVertices()];              //nombre de chemins optimaux pour attendre le sommet

        for (int i = 0; i < graph.nbVertices(); ++i) {  //initialisation des valeurs par défaut
            distanceToSource[i] = -1;
            parent[i] = -1;
            optimalPaths[i] = 0;
        }
        // initialisation des valeurs de la source
        distanceToSource[source] = 0;
        optimalPaths[source] = 1;
        distances.setLabel(source, 0);
        // file d'attente pour le parcours en largeur
        ArrayDeque<Integer> queue = new ArrayDeque<Integer>(graph.nbVertices());
        queue.addLast(source);    // insertion de la source dans la file

        while (!queue.isEmpty()) {     // si la queue n'est pas vide
            int current = queue.removeFirst();   // enlever le prochain sommet de la file
            if (current == destination) { // si c'est la destination on sort de la boucle
                break;
            }
            // pour tous ses voisins on regarde s'ils ont déjà été découverts ou pas
            for (int neighbour : graph.neighbors(current)) {
                if (distanceToSource[neighbour] == -1) { // decouverte, on met à jour les valeurs et on l'ajoute à la file
                    distanceToSource[neighbour] = distanceToSource[current] + 1;
                    parent[neighbour] = current;
                    optimalPaths[neighbour] = optimalPaths[current];
                    distances.setLabel(neighbour, distanceToSource[neighbour]);
                    queue.addLast(neighbour);
                } else if (distanceToSource[neighbour] == distanceToSource[current] + 1) { //parcours alternatif
                    optimalPaths[neighbour] += optimalPaths[current]; // on ajoute nos chemins optimaux à ceux déjà trouvés pour ce sommet
                }
            }
        }
        return new BfsResult(distanceToSource, parent, optimalPaths);
    }
}
