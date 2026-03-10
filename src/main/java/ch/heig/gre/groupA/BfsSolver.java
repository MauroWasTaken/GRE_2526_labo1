package ch.heig.gre.groupA;

import ch.heig.gre.Keys;
import ch.heig.gre.graph.Graph;
import ch.heig.gre.graph.GridGraph2D;
import ch.heig.gre.graph.PositiveWeightFunction;
import ch.heig.gre.graph.VertexLabelling;
import ch.heig.gre.maze.MazeSolver;
import ch.heig.gre.maze.Metadata;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;

public final class BfsSolver implements MazeSolver {
    @Override
    public Result solve(GridGraph2D grid, PositiveWeightFunction weights, int source, int destination, VertexLabelling<Integer> distances) {
        // Ne pas modifier
        return solve((Graph) grid, source, destination, distances);
    }

    private class BfsResult { // classe interne pour le retour de la méthode BFS
        int[] d;
        Integer[] p;
        int[] N;

        BfsResult(int[] d, Integer[] p, int[] N) {
            this.d = d;
            this.p = p;
            this.N = N;
        }
    }

    public Result solve(Graph graph, int source, int destination, VertexLabelling<Integer> distances) {
        BfsResult result = BFS(graph, source, destination, distances);                                              //calcul des distances, parents et nombre de chemins optimaux

        Metadata metadata = new Metadata();                                                                         // stockage des métadonnées
        metadata.put(Keys.LENGTH, result.d[destination]);
        metadata.put(Keys.NB_OPTIMAL_PATHS, (long) result.N[destination]);


        if (result.p[destination] == null) return new Result(new ArrayList<Integer>(), metadata);                   // cas ou aucun chemin a été trouvé


        //preparation du chemin à retourner
        ArrayList<Integer> path = new ArrayList<>(Collections.nCopies(result.d[destination] + 1, null));      // declaration du chemin à retourner dans un tableau avec la bonne taille
        path.set(result.d[destination],destination);                                                                // on met la destination à la fin du chemin
        int i = 1;                                                                                                  // index pour savoir ou inserer les sommets
        int to = result.p[destination];                                                                             // on prend le parent de la destination
        while(to != source){                                                                                        // tant que le parent n'est pas la source on l'ajoute a la fin de la liste
            path.set(result.d[destination] - i, to);
            to = result.p[to];
            i++;
        }
        path.set(0,source);                                                                                         // on ajoute la source au début du chemin

        return new Result(path,metadata);
    }

    private BfsResult BFS(Graph graph, int source, int destination, VertexLabelling<Integer> distances) {
        int[] d = new int[graph.nbVertices()];          //distance avec le sommet source, -1 si non encore trouvé
        Integer[] p = new Integer[graph.nbVertices()];  //parant du sommet (celui qui a permis de le découvrir), null si non encore trouvé
        int[] N = new int[graph.nbVertices()];          //nombre de chemins optimaux pour attendre le sommet

        for (int i = 0; i < graph.nbVertices(); ++i) {  //initialisation des valeurs par défaut
            d[i] = -1;
            p[i] = null;
            N[i] = 0;
        }
        // initialisation des valeurs de la source
        d[source] = 0;
        N[source] = 1;
        distances.setLabel(source, 0);
        // file d'attente pour le parcours en largeur
        ArrayList<Integer> Q = new ArrayList<Integer>(Collections.nCopies(graph.nbVertices(), null));
        int in = 0;             // pseudo pointeur vers la prochaine position d'insertion dans la file
        int out = 0;            // pseudo pointeur vers la prochaine position de lecture dans la file

        Q.set(in++, source);    // insertion de la source dans la file
        while (in != out) {     // si la queue n'est pas vide
            int u = Q.get(out++);   // enlever le prochain sommet de la file
            if (u == destination) { // si c'est la destination on sort de la boucle
                break;
            }
            // pour tous ses voisins on regarde s'ils ont déjà été découverts ou pas
            for (int v : graph.neighbors(u)) {
                if (d[v] == -1) { // decouverte, on met à jour les valeurs et on l'ajoute à la file
                    d[v] = d[u] + 1;
                    p[v] = u;
                    N[v] = N[u];
                    distances.setLabel(v, d[v]);
                    Q.set(in++, v);
                } else if (d[v] == d[u] + 1) { //parcours alternatif
                    N[v] += N[u]; // on ajoute nos chemins optimaux à ceux déjà trouvés pour ce sommet
                }
            }
        }
        return new BfsResult(d, p, N);
    }
}
