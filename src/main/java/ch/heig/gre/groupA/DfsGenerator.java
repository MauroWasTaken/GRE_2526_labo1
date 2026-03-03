package ch.heig.gre.groupA;

import ch.heig.gre.graph.Graph;
import ch.heig.gre.maze.MazeBuilder;
import ch.heig.gre.maze.MazeGenerator;
import ch.heig.gre.maze.Progression;
import ch.heig.gre.util.ArrayUtil;

import java.util.*;

// TODO : classe à compléter et documenter
public final class DfsGenerator implements MazeGenerator {
	@Override
	public void generate(MazeBuilder builder, int from) {
		// Mise à jour de l'interface graphique :
		// builder.progressions().setLabel(..., ...);

		Graph graph = builder.topology();
		int[] start = new int[graph.nbVertices()], end = new int[graph.nbVertices()], p = new int[graph.nbVertices()];
		int date = 0;
		Deque<int[]> stack = new ArrayDeque<>();
		for (int i = 0; i < graph.nbVertices(); ++i) {
			if (start[i] == 0) {
				stack.push(new int[]{i, -1}); // {noeud, parent}

				while (!stack.isEmpty()) {
					int[] current = stack.pop();
					int u = current[0];
					int parent = current[1];

					if (u < 0) {
						// Phase "après" : on récupère le vrai noeud
						int realU = -u - 1;
						++date;
						end[realU] = date;
						builder.progressions().setLabel(realU, Progression.PROCESSED);
						continue;
					}

					if (start[u] != 0) continue;

					if (parent != -1) {
						builder.removeWall(parent, u); // on détruit le mur
					}

					++date;
					start[u] = date;
					builder.progressions().setLabel(u, Progression.PROCESSING);

					stack.push(new int[]{-u - 1, -1}); // marqueur fin

					int[] neighbors = graph.neighbors(u);
					ArrayUtil.shuffle(neighbors);

					for (int v : neighbors) {
						if (start[v] == 0) {
							p[v] = u;
							stack.push(new int[]{v, u}); // on stocke le parent
						}
					}
				}

			}
		}
	}
}
