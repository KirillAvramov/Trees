package Trees.RBTree

import Trees.Interface.Node
import Trees.Interface.Tree


open class NodeRBTree<K: Comparable<K>, V> (k: K, v: V): Node<K, V> {
    override var value: V = v
    override var key: K = k
    var color: Boolean = false  //Black

    var father: NodeRBTree<K, V>? = null
    var leftChild: NodeRBTree<K, V>? = null
    var rightChild: NodeRBTree<K, V>? = null
}

class RBTree<K: Comparable<K>, V>: Tree<K, V, NodeRBTree<K, V>> {

    override var root: NodeRBTree<K, V>? = null

    override fun insert(key: K, value: V, node: NodeRBTree<K, V>?) { // аргумент node - это root
        if (node == null) {
            root = NodeRBTree(key, value)
            return
        }
        if (node.key == key) {
            println("Вершина уже есть в дереве")
            return
        }
        val t: Boolean = key < node.key

        if (t && node.leftChild != null) insert(key, value, node.leftChild)
        else if (t.not() && node.rightChild != null) insert(key, value, node.rightChild)
        else {
            if (t) {
                node.leftChild = NodeRBTree(key, value)
                node.leftChild!!.color = true
                node.leftChild!!.father = node
                balanceAfterInsert(node.leftChild!!)
            }
            else if (t.not()){
                node.rightChild = NodeRBTree(key, value)
                node.rightChild!!.color = true
                node.rightChild!!.father = node
                balanceAfterInsert(node.rightChild!!)
            }
        }
    }

    private fun balanceAfterInsert (v: NodeRBTree<K, V>?) {
        if (v!!.father == null && v.color) {
            v.color = false
            return
        }
        if (v.father!!.color) {
            if (v == v.father!!.rightChild && v.father == v.father!!.father?.leftChild ||
                    v == v.father!!.leftChild && v.father == v.father!!.father?.rightChild) {
                turn(v.key)
                turn(v.key)
                balanceAfterInsert(v.leftChild)
            }
            else turn(v.father!!.key)
        }
        if (v.father!!.leftChild != null && v.father!!.rightChild != null) {
            if (v.father!!.leftChild!!.color && v.father!!.rightChild!!.color) {
                v.father!!.leftChild!!.color = false; v.father!!.rightChild!!.color = false
                v.father!!.color = true
                balanceAfterInsert(v.father!!)
            }
        }
    }

    override fun find(key: K): NodeRBTree<K, V>? {
        var vertex = root
        while (vertex != null) {
            if (key < vertex.key) vertex = vertex.leftChild
            else if (key > vertex.key) vertex = vertex.rightChild
            else if (key == vertex.key) return vertex
        }
        return vertex
    }

    private fun turn (key: K) {  // Поворот вершины со значением key вокруг его родителя
        val v: NodeRBTree<K, V>? = find(key)
        if (v == null || v.father == null) return

        val color = v.color
        v.color = v.father!!.color
        v.father!!.color = color


        if (v.father!!.father != null) {
            if (key < v.father!!.father!!.key) v.father!!.father!!.leftChild = v
            else v.father!!.father!!.rightChild = v
        } else root = v //Здесь могут быть ошибки, когда поворачиваем вершину вокруг корня, проверить root надо

        if (key < v.father!!.key ) {

            v.father!!.leftChild = v.rightChild
            v.rightChild?.father = v.father
            v.rightChild = v.father
            v.father = v.father!!.father
            v.rightChild!!.father = v
        }

        else if (key > v.father!!.key) {

            v.father!!.rightChild = v.leftChild
            v.leftChild?.father = v.father
            v.leftChild = v.father
            v.father = v.father!!.father
            v.leftChild!!.father = v
        }

    }

    override fun remove(key: K) {
        val vertex: NodeRBTree<K, V>? = find(key) ?: return
        val color: Boolean = vertex!!.color
        var vertex2: NodeRBTree<K, V>?

        if (vertex == root && (vertex.rightChild == null || vertex.leftChild == null)) {
            root = null
            return
        }
        // Вариант без потомков
        if (vertex.rightChild == null && vertex.leftChild == null) {

                if (key < vertex.father!!.key) vertex.father!!.leftChild = null
                else vertex.father!!.rightChild = null
                if (!color) balanceAfterRemove(vertex.father, key)
                vertex.father = null

        }
        // Вариант с двумя потомками
        else if (vertex.rightChild != null && vertex.leftChild != null) {

            vertex2 = vertex.rightChild
            while (vertex2!!.leftChild != null) vertex2 = vertex2.leftChild
            val k = vertex2.key
            val v = vertex2.value
            remove(k)
            vertex.key = k; vertex.value = v
        }
        // Вариант с одним потомком
        else {

            vertex2 = if (vertex.leftChild != null) vertex.leftChild else vertex.rightChild
            if (vertex.father == null) {
                vertex2!!.father = null
                vertex.leftChild = null; vertex.rightChild = null
                root = vertex2
                root!!.color = false
            } else {

                if (key < vertex.father!!.key) vertex.father!!.leftChild = vertex2
                else vertex.father!!.rightChild = vertex2
                vertex2!!.father = vertex.father
                vertex.father = null
                vertex.leftChild = null
                vertex.rightChild = null
                if (!color) balanceAfterRemove (vertex2.father, key)
            }
        }
    }

    private fun balanceAfterRemove (vertex: NodeRBTree<K, V>?, key: K) { // Вершина-аргумент - это предок удаленного узла, а ключ - это ключ удаленного узла
        if (vertex == null) return
        val vertex2 = (if (key < vertex.key) vertex.rightChild else vertex.leftChild) ?: return //т.к. дерево сбалансированное и если брата нет, то мы удалили красную вершину

        //Случаи, когда потомок удаленной вершины красный
        if (vertex.leftChild != vertex2 && vertex.leftChild?.color == true) vertex.leftChild!!.color = false
        else if (vertex.rightChild != vertex2 && vertex.rightChild?.color == true) vertex.rightChild!!.color = false

        // Случай, когда у брата нет потомков
        else if (vertex2.leftChild == null && vertex2.rightChild == null) {
            vertex2.color = true
            if (vertex.color) vertex.color = false
            else if (!vertex.color && vertex.father != null) balanceAfterRemove(vertex.father, key)
        }

        //Случаи, когда предок удаленной вершины красный
        else if (vertex.color) {

            // Вариант, когда у брата удаленной вершины нет красных потомков
            if ((vertex2.rightChild == null || vertex2.rightChild?.color == false) && (vertex2.leftChild == null || vertex2.leftChild?.color == false)) {
                vertex.color = false
                vertex2.color = true
            }
            //Вариант, когда у брата удаленной вершины есть хотя бы один красный потомок
            else if (vertex.rightChild == vertex2) {
                if (vertex2.rightChild?.color == true) {
                    turn(vertex2.key)
                    vertex2.color = false
                    vertex.color = true
                    balanceAfterInsert(vertex)
                } else if (vertex2.leftChild?.color == true) {
                    turn(vertex2.leftChild!!.key)
                    turn(vertex2.father!!.key)
                    vertex.color = true
                    vertex2.father!!.color = false
                    balanceAfterInsert(vertex)
                }
            } else if (vertex.leftChild == vertex2) {
                if (vertex2.leftChild?.color == true) {
                    turn(vertex2.key)
                    vertex2.color = false
                    vertex.color = true
                    balanceAfterInsert(vertex)
                } else if (vertex2.rightChild?.color == true) {
                    turn(vertex2.rightChild!!.key)
                    turn(vertex2.father!!.key)
                    vertex.color = true
                    vertex2.father!!.color = false
                    balanceAfterInsert(vertex)
                }
            }


        }

        // Случаи, когда предок удаленной вершины черный
        else {

            //Вариант, когда брат удаленной вершины красный
            if (vertex2.color)  {
                turn(vertex2.key)
                balanceAfterRemove(vertex, key)
            }

            //Вариант, когда брат удаленой вершины черный
            else {
                // Потомки брата оба черные
                if (vertex2.leftChild?.color == false && vertex2.rightChild?.color == false) {
                    vertex2.color = true
                    balanceAfterRemove(vertex.father, key)
                }
                else {

                    if (vertex.rightChild == vertex2) {
                        if (vertex2.rightChild?.color == true) {
                            turn(vertex2.key)
                            vertex2.rightChild?.color = false
                        } else if (vertex2.leftChild?.color == true){
                            turn(vertex2.leftChild!!.key)
                            vertex2.color = false
                            vertex2.father!!.color = true
                            turn(vertex2.father!!.key)
                            vertex2.color = false
                        }
                    }
                    else if (vertex.leftChild == vertex2) {
                        if (vertex2.leftChild?.color == true) {
                            turn(vertex2.key)
                            vertex2.leftChild?.color = false
                        } else if (vertex2.rightChild?.color == true){
                            turn(vertex2.rightChild!!.key)
                            vertex2.color = false
                            vertex2.father!!.color = true
                            turn(vertex2.father!!.key)
                            vertex2.color = false
                        }
                    }
                }

            }

        }

    }

    fun printRBTree () {
        print(mutableListOf(root!!), root!!, 0)
        println()
    }
    private fun print (line: MutableList<NodeRBTree<K, V>?>, node: NodeRBTree<K, V>?, i: Int) {
        var k: Int
        if (i < 0) k = line.size-1
        else k = i
        line.removeAt(0)
        k--
        if (node != null) {
            line.add(node.leftChild)
            line.add(node.rightChild)
        }
        if (node != null) if (node.color) print("[")
        print("${node?.key}")
        if (node != null) if (node.color) print("]")
        print("     ")

        if (k < 0) println()
        if (line.size > 0) print(line, line[0] ,k)
    }

    open class RBTreeIerator<K : Comparable<K>, V>(v: NodeRBTree<K, V>?) : Iterator<NodeRBTree<K, V>> {
        var node = v
        private var next: NodeRBTree<K, V>? = null
        private var vertex: NodeRBTree<K, V>? = null
        private fun minKey(v: NodeRBTree<K, V>): NodeRBTree<K, V> {
            var tmp = v
            while (tmp.leftChild != null) tmp = tmp.leftChild!!
            return tmp
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
                var prev = vertex
                var isRightChild: Boolean = true
                if (vertex!!.father != null) isRightChild = vertex!!.father!!.rightChild == vertex
                else isRightChild = false
                while (vertex!!.leftChild != prev && (vertex!!.father != null || isRightChild)) {
                    prev = vertex
                    vertex = vertex!!.father
                    if (vertex!!.father != null) isRightChild = vertex!!.father!!.rightChild == vertex
                    else isRightChild = false
                }
                val checkout = (next!!.key < vertex!!.key)
                next = vertex
                return checkout
            }
            else {
                next = vertex!!.father
                return (next != null)
            }

        }

        override fun next(): NodeRBTree<K, V> = next!!
    }
}
