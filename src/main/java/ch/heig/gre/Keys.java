package ch.heig.gre;

import ch.heig.gre.maze.Metadata;

/**
 * Contient les clés des métadonnées nécessaire à ce laboratoire.
 */
public final class Keys {
  private Keys() {}

  /**
   * Clé de la métadonnée contenant la longueur (nombre d'arêtes) de la chaîne du plus court itinéraire trouvé par
   * l'algorithme de résolution.
   */
  public static final Metadata.Key<Integer> LENGTH = new Metadata.Key<>("Longueur", Integer.class);

  /** Clé de la métadonnée contenant le nombre de chaînes de longueur optimales entre la source et la destination. */
  public static final Metadata.Key<Long> NB_OPTIMAL_PATHS = new Metadata.Key<>("Nb itinéraires optimaux", Long.class);
}
