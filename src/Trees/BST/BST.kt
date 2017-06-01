package Trees.BST

import Trees.Interface.Node
import Trees.Interface.Tree

open class NodeBST <K: Comparable<K>, V> (k: K, v: V): Node<K, V> {
    override var value: V = v
    override var key: K = k

    var father: NodeBST<K, V>? = null
    var leftChild: NodeBST<K, V>? = null
    var rightChild: NodeBST<K, V>? = null
}

class BST<K: Comparable<K>, V>: Tree<K, V, NodeBST<K, V>> {

    override var root: NodeBST<K, V>? = null

    override fun insert(key: K, value: V, node: NodeBST<K, V>?) { // аргумент node - это root
        if (node == null) {
            root = NodeBST(key, value)
            return
        }
        if (key == node.key) return println("Вершина уже есть в дереве")
        val t: Boolean = key < node.key
        if (t && node.leftChild != null) insert(key, value, node.leftChild)
        else if (t.not() && node.rightChild != null) insert(key, value, node.rightChild)
        else {
            if (t) {
                node.leftChild = NodeBST(key, value)
                node.leftChild!!.father = node
            }
            else {
                node.rightChild = NodeBST(key, value)
                node.rightChild!!.father = node
            }
        }
    }

    override fun find(key: K): NodeBST<K, V>? {
        var vertex = root
        while (vertex != null) {
            if (key < vertex.key) vertex = vertex.leftChild
            else if (key > vertex.key) vertex = vertex.rightChild
            else if (key == vertex.key) return vertex
        }
        return vertex
    }

    override fun remove(key: K) {
        var vertex: NodeBST<K, V>? = find(key) ?: return println("Ключ не найден")
        if (vertex!!.father == null && (vertex.rightChild == null || vertex.leftChild == null)) {
            vertex = null
            return
        }
        // Вариант без детей
        if (vertex.rightChild == null && vertex.leftChild == null) {
            if (vertex.father == null) vertex = null
            else {
                if (key < vertex.father!!.key) vertex.father!!.leftChild = null
                else vertex.father!!.rightChild = null
                vertex.father = null
            }
        }
        // Вариант с двумя детьми
        else if (vertex.rightChild != null && vertex.leftChild != null) {
            var vertex2 = vertex.rightChild
            while (vertex2!!.leftChild != null) vertex2 = vertex2.leftChild
            val k = vertex2.key
            val v = vertex2.value
            remove(k)
            vertex.key = k; vertex.value = v
        }
        // Вариант с одним ребенком
        else {
            val vertex2 = if (vertex.leftChild != null) vertex.leftChild else vertex.rightChild
            if (vertex.father == null) {
                vertex2!!.father = null
                vertex = vertex2
                root = vertex
            } else {
                if (key < vertex.father!!.key) vertex.father!!.leftChild = vertex2
                else vertex.father!!.rightChild = vertex2
                vertex2!!.father = vertex.father
                vertex.father = null
                if (vertex.leftChild != null) vertex.leftChild = null
                else vertex.rightChild = null
            }
        }
    }

    fun printBST () {
        print(mutableListOf(root!!), root!!, 0)
        println()
    }
    private fun print (line: MutableList<NodeBST<K, V>?>, node: NodeBST<K, V>?, i: Int) {
        var k: Int
        if (i < 0) k = line.size-1
        else k = i
        line.removeAt(0)
        k--
        if (node != null) {
            line.add(node.leftChild)
            line.add(node.rightChild)
        }
        print("[")
        print("${node?.key}")
        print("]")
        print("     ")

        if (k < 0) println()
        if (line.size > 0) print(line, line[0] ,k)
    }


    open class BSTIterator<K : Comparable<K>, V>(v: NodeBST<K, V>?) : Iterator<NodeBST<K, V>> {
        var node = v
        private var next: NodeBST<K, V>? = null
        private var vertex: NodeBST<K, V>? = null
        private fun minKey(node: NodeBST<K, V>): NodeBST<K, V> {
            var vertexMin = node
            while (vertexMin.leftChild != null) vertexMin = vertexMin.leftChild!!
            return vertexMin
        }

        override fun hasNext(): Boolean {
            if (node == null) {
                return false
            }
            if (next == null) {
                next = minKey(node!!)
                return true
            }
            vertex = next
            if (vertex!!.rightChild != null) {
                next = minKey(vertex!!.rightChild!!)
                return true
            }
            if (vertex!!.father != null && vertex!!.father!!.rightChild == vertex) {
                var v2 = vertex
                var isRightChild: Boolean = true
                if (vertex!!.father != null) isRightChild = vertex!!.father!!.rightChild == vertex
                else isRightChild = false

                while (vertex!!.leftChild != v2 && (vertex!!.father != null || isRightChild)) {
                    v2 = vertex
                    vertex = vertex!!.father
                    if (vertex!!.father != null) isRightChild = vertex!!.father!!.rightChild == vertex
                    else isRightChild = false
                }

                val checkout = (next!!.key < vertex!!.key)
                next = vertex
                return (checkout)
            } else {
                next = vertex!!.father
                return (next != null)
            }

        }

        override fun next(): NodeBST<K, V> = next!!
    }

}

