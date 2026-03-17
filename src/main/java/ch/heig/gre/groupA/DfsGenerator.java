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

		Graph topology = builder.topology();

		// Tableau qui contiendra un boolean indiquant si un sommet a été visité ou non
		boolean[] visited = new boolean[topology.nbVertices()];

		// La pile simule la pile d'appels recursive.
		// On empile deux etats par sommet: entree (pre-visit) puis sortie (post-visit).
		Deque<DfsFrame> dfsStack = new ArrayDeque<>(topology.nbVertices() * 2); // *2 pour les marqueurs d'entree et de sortie
		dfsStack.push(DfsFrame.preVisit(from, -1));

		// On n'a pas besoin de boucle for en dehors, car on sait que le graphe est connexe, donc tous les sommets seront visités à partir du sommet "from"
		// On démarre la DFS à partir du sommet "from", sans parents (indiqué par -1)
		while (!dfsStack.isEmpty()) {
			DfsFrame frame = dfsStack.pop();

			if (frame.isPostVisit) {
				builder.progressions().setLabel(frame.vertex, Progression.PROCESSED);
				continue;
			}

			// Si le noeud a déjà été visité, on passe au suivant dans la pile
			if (visited[frame.vertex]) continue;

			if (frame.parentVertex != -1) {
				builder.removeWall(frame.parentVertex, frame.vertex);
			}

			visited[frame.vertex] = true;
			builder.progressions().setLabel(frame.vertex, Progression.PROCESSING);

			// Etat de sortie du sommet, equivalent au retour d'un appel recursif.
			dfsStack.push(DfsFrame.postVisit(frame.vertex));

			// On mélange les voisins
			// Sera exécuté qu'une seule fois par noeud car on l'a marqué comme visité avant
			int[] shuffledNeighbors = topology.neighbors(frame.vertex);
			ArrayUtil.shuffle(shuffledNeighbors);

			for (int neighbor : shuffledNeighbors) {
				if (!visited[neighbor]) {
					dfsStack.push(DfsFrame.preVisit(neighbor, frame.vertex));
				}
			}
		}
	}
}

// Classe pour stocker les informations de la pile : le noeud actuel et son parent, ainsi que si c'est une visite pré- ou post-ordre
final class DfsFrame {
	final int vertex;
	final int parentVertex;
	final boolean isPostVisit;

	private DfsFrame(int vertex, int parentVertex, boolean isPostVisit) {
		this.vertex = vertex;
		this.parentVertex = parentVertex;
		this.isPostVisit = isPostVisit;
	}

	static DfsFrame preVisit(int vertex, int parentVertex) {
		return new DfsFrame(vertex, parentVertex, false);
	}

	static DfsFrame postVisit(int vertex) {
		return new DfsFrame(vertex, -1, true);
	}
}