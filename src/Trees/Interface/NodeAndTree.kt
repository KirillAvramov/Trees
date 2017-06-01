package Trees.Interface

interface Node<K: Comparable<K>, V> {

    var value: V
    var key: K
}

interface Tree<in K: Comparable<K>, in V, N> {

    var root: N?

    fun insert(key: K, value: V, node: N? = root)
    fun find(key: K): N?
    fun remove(key: K)
}
