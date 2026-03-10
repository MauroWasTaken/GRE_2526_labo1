// Maikol Correia Da Silva et Mauro Santos

package ch.heig.gre.groupA;

import ch.heig.gre.graph.Graph;
import ch.heig.gre.maze.MazeBuilder;
import ch.heig.gre.maze.MazeGenerator;
import ch.heig.gre.maze.Progression;
import ch.heig.gre.util.ArrayUtil;

import java.util.ArrayDeque;
import java.util.Deque;

public final class DfsGenerator implements MazeGenerator {
	@Override
	public void generate(MazeBuilder builder, int from) {

		Graph graph = builder.topology();

		// Tableau qui contiendra un boolean indiquant si un sommet a été visité ou non
		boolean[] visited = new boolean[graph.nbVertices()];

		// Pile pour la DFS itérative, contenant des tableaux de 2 éléments : {noeud, parent}
		Deque<StackInfo> stack = new ArrayDeque<>(graph.nbVertices() * 2); // *2 pour les marqueurs de fin

		// On démarre la DFS à partir du sommet "from", sans parent (indiqué par -1)
		stack.push(new StackInfo(from, -1)); // {noeud, parent}

		while (!stack.isEmpty()) {
			StackInfo current = stack.pop();

			if (current.node < 0) {
				// Phase "après" comme si on remontait de la récursion : on récupère le vrai noeud
				current.node = -current.node - 1;
				builder.progressions().setLabel(current.node, Progression.PROCESSED);
				continue;
			}

			// Si le noeud a déjà été visité, on passe au suivant dans la pile
			if (visited[current.node]) continue;

			if (current.parent != -1) {
				builder.removeWall(current.parent, current.node); // on détruit le mur
			}

			visited[current.node] = true;
			builder.progressions().setLabel(current.node, Progression.PROCESSING);

			// marqueur fin, pour pouvoir le traiter après, comme si on remontait de la récursion
			// On inverse le signe du noeud pour le différencier d'un vrai noeud, et on soustrait 1 pour éviter les confusions avec le noeud 0
			stack.push(new StackInfo(-current.node - 1, -1));

			// On mélange les voisins
			// Sera exécuté qu'une seule fois par noeud car on l'a marqué comme visité avant
			int[] neighbors = graph.neighbors(current.node);
			ArrayUtil.shuffle(neighbors);

			for (int v : neighbors) {
				if (!visited[v]) {
					stack.push(new StackInfo(v, current.node)); // on stocke le nouveau noeud et son parent dans la pile
				}
			}
		}

	}
}

// Classe pour stocker les informations de la pile : le noeud actuel et son parent
final class StackInfo {
	int node;
	int parent;

	public StackInfo(int node, int parent) {
		this.node = node;
		this.parent = parent;
	}
}