package Trees.BTree

import Trees.Interface.Node
import Trees.Interface.Tree

class NodeBTree<K: Comparable<K>, V> (k: K, v: V): Node<K, V> {
    override var value: V = v
    override var key: K = k

    var leftChild: VertexBTree<K, V>? = null
    var rightChild: VertexBTree<K, V>? = null
}

open class VertexBTree<K: Comparable<K>, V> {
    var nodes: MutableList<NodeBTree<K, V>> = mutableListOf()
    var father: VertexBTree<K, V>? = null

    fun insertNode (key: K, value: V, t: Int?, v: NodeBTree<K, V>?) { // Добавление обычной вершины в вершину би-дерева
        if (nodes.size == 0) {
            if (v != null) nodes.add(v)
            else nodes.add(NodeBTree(key, value))
            return
        }

        var numb: Int? = t
        if (numb == null) {
            var k: Int = 0
            var i: Int = nodes.size - 1
            while (i-k > 1) {
                if (key > nodes[(k+i)/2].key) k = (k+i)/2
                else i = (k+i)/2
            }
            if (key < nodes[k].key) numb = k
            else if (key > nodes[i].key) numb = i+1
            else numb = i
        }
        if (v == null) nodes.add(numb, NodeBTree(key, value))
        else nodes.add(numb, v)
        if (numb == nodes.size-1) nodes[numb - 1].rightChild = nodes[numb].leftChild
        else if (numb == 0) nodes[numb + 1].leftChild = nodes[numb].rightChild
        else {
            nodes[numb - 1].rightChild = nodes[numb].leftChild
            nodes[numb + 1].leftChild = nodes[numb].rightChild
        }
    }
}

class BTree<K: Comparable<K>, V>(minDeg: Int): Tree<K, V, VertexBTree<K, V>> {
    val maxNumberOfKeys = 2*minDeg-2
    val minNumberOfKeys = minDeg-1
    override var root: VertexBTree<K, V>? = VertexBTree()

    override fun insert(key: K, value: V, v: VertexBTree<K, V>?) { // аргумент node - это root
        if (v!!.nodes.size == 0) {
            v.insertNode(key, value, 0, null)
            return
        }

        var k: Int = 0
        var i: Int = v.nodes.size - 1
        while (i-k > 1) {
            if (key > v.nodes[(k+i)/2].key) k = (k+i)/2
            else i = (k+i)/2
        }
        if (key < v.nodes[k].key) k += 0
        else if (key > v.nodes[i].key && i != v.nodes.size-1) k = i+1
        else if (key == v.nodes[i].key || key == v.nodes[k].key) return println("Вершина в дереве уже есть)")
        else k = i

        if (v.nodes[0].leftChild == null && v.nodes[0].rightChild == null) {
            if (i != v.nodes.size-1) if (key == v.nodes[i+1].key) return
            if (key > v.nodes[i].key) v.insertNode(key, value, k+1, null)
            else v.insertNode(key, value, k, null)
            balanceAfterInsert(v)
        }
        else {
            if (key < v.nodes[k].key) insert(key, value, v.nodes[k].leftChild)
            else insert(key, value, v.nodes[k].rightChild)
        }
    }

    private fun balanceAfterInsert(vertex: VertexBTree<K, V>) {
        val size = vertex.nodes.size
        if (maxNumberOfKeys >= size) return

        vertex.nodes[size/2].leftChild = VertexBTree()
        for (t in 0 until size/2) {
            vertex.nodes[size/2].leftChild!!.nodes.add(vertex.nodes[t])
            vertex.nodes[t].leftChild?.father = vertex.nodes[size/2].leftChild
            if (t+1 == size/2) {
                vertex.nodes[t].rightChild?.father = vertex.nodes[size/2].leftChild

            }
        }

        vertex.nodes[size/2].rightChild = VertexBTree()
        for (t in size/2+1 until size) {
            vertex.nodes[size/2].rightChild!!.nodes.add(vertex.nodes[t])
            vertex.nodes[t].leftChild?.father = vertex.nodes[size/2].rightChild
            if (t+1 == size) {
                vertex.nodes[t].rightChild?.father = vertex.nodes[size/2].rightChild

            }
        }

        if (vertex.father == null) {
            vertex.nodes[size/2].leftChild!!.father = vertex
            vertex.nodes[size/2].rightChild!!.father = vertex
            vertex.nodes.retainAll(listOf(vertex.nodes[size/2]))
            return
        } else {
            vertex.nodes[size/2].leftChild!!.father = vertex.father
            vertex.nodes[size/2].rightChild!!.father = vertex.father
            vertex.father!!.insertNode(vertex.nodes[size/2].key, vertex.nodes[size/2].value, null, vertex.nodes[size/2])
            balanceAfterInsert(vertex.father!!)
            vertex.father = null
            vertex.nodes.clear()
            return
        }
    }

    override fun find(key: K): VertexBTree<K, V>? {
        var vertex = root
        while (vertex != null) {
            for (element in vertex!!.nodes) {
                if (key < element.key) {
                    vertex = element.leftChild
                    break
                }
                else if (key > element.key && element == vertex.nodes.last()) {
                    vertex = element.rightChild
                    break
                }
                else if (key == element.key) return vertex
            }
        }
        return vertex
    }

    override fun remove(key: K) {
        balanceAfterRemove(find(key), key)
    }

    private fun balanceAfterRemove (v: VertexBTree<K, V>?, key: K): Boolean {
        if (v == null || (v.nodes.size < minNumberOfKeys && v.father != null)) {
            println("Дерево построено неправильно!")
            return true
        }

        var number: Int = -1
        for (i in 0 until v.nodes.size) {
            if (v.nodes[i].key == key) {
                number = i
                break
            }
        }

        if (v.nodes[0].rightChild == null && v.nodes.size > minNumberOfKeys) {
            v.nodes.removeAt(number)
            return false
        }
        else if (v.nodes[0].rightChild == null && v.nodes.size <= minNumberOfKeys) {
            if (v == root) {
                v.nodes.removeAt(number)
                return false
            }

            var v2 = v.father
            var ind: Int = 0
            if (v2!!.nodes.size <= minNumberOfKeys) {
                while (v2!!.father != null && v2.father?.nodes!!.size <= minNumberOfKeys) v2 = v2.father
                if (v2.father != null) v2 = v2.father
                while (v2 != v.father) {
                    while (v2!!.nodes[ind].key < key && ind != (v2.nodes.size - 1)) ind += 1
                    v2 = joining(v2.nodes[ind].leftChild!!, v2.nodes[ind].rightChild!!)
                    if (v2.father != null) balanceAfterInsert(v2.father!!)
                    ind = 0
                }
            }

            for (i in 0 until v.father!!.nodes.size) {
                if (v.father!!.nodes[i].leftChild == v || v.father!!.nodes[i].rightChild == v) {
                    ind = i
                    break
                }
            }

            if (v.father!!.nodes[ind].leftChild!!.nodes.size > minNumberOfKeys) {
                v.nodes.removeAt(number)
                v.nodes.add(0, NodeBTree(v.father!!.nodes[ind].key, v.father!!.nodes[ind].value))
                v.father!!.nodes[ind].key = v.father!!.nodes[ind].leftChild!!.nodes.last().key
                v.father!!.nodes[ind].value = v.father!!.nodes[ind].leftChild!!.nodes.last().value
                v.father!!.nodes[ind].leftChild!!.nodes.remove(v.father!!.nodes[ind].leftChild!!.nodes.last())
                if (v.father != null) balanceAfterInsert(v.father!!)
                return false
            }
            if (v.father!!.nodes[ind].rightChild!!.nodes.size > minNumberOfKeys) {
                v.nodes.removeAt(number)
                v.nodes.add(NodeBTree(v.father!!.nodes[ind].key, v.father!!.nodes[ind].value))
                v.father!!.nodes[ind].key = v.father!!.nodes[ind].rightChild!!.nodes[0].key
                v.father!!.nodes[ind].value = v.father!!.nodes[ind].rightChild!!.nodes[0].value
                v.father!!.nodes[ind].rightChild!!.nodes.removeAt(0)
                if (v.father != null) balanceAfterInsert(v.father!!)
                return false
            }
            if (ind != v.father!!.nodes.size-1 && v != v.father!!.nodes[ind].leftChild) {
                if (v.father!!.nodes[ind+1].rightChild!!.nodes.size > minNumberOfKeys) {
                    v.nodes.removeAt(number)
                    v.nodes.add(NodeBTree(v.father!!.nodes[ind+1].key, v.father!!.nodes[ind+1].value))
                    v.father!!.nodes[ind+1].key = v.father!!.nodes[ind+1].rightChild!!.nodes[0].key
                    v.father!!.nodes[ind+1].value = v.father!!.nodes[ind+1].rightChild!!.nodes[0].value
                    v.father!!.nodes[ind+1].rightChild!!.nodes.removeAt(0)
                    if (v.father != null) balanceAfterInsert(v.father!!)
                    return false
                }
            }

            v2 = joining(v.father!!.nodes[ind].leftChild!!, v.father!!.nodes[ind].rightChild!!)
            for (i in 0 until v2.nodes.size) {
                if (v2.nodes[i].key == key) {
                    number = i
                    break
                }
            }
            v2.nodes.removeAt(number)
            if (v2.father != null) balanceAfterInsert(v2.father!!)
            return false
        }
        else {
            var v2 = v.nodes[number].leftChild
            while (v2!!.nodes.last().rightChild != null) v2 = v2.nodes.last().rightChild
            val key2 = v2.nodes.last().key
            val value2 = v2.nodes.last().value
            balanceAfterRemove(v2, key2)
            v2 = find(key)
            for (i in 0 until v2!!.nodes.size) {
                if (v2.nodes[i].key == key) {
                    number = i
                    break
                }
            }
            v2.nodes[number].key = key2
            v2.nodes[number].value = value2
        }
        return true
    }

    //Функция соеденения двух соседних вершин дерева, где vertex1 - левая вершина, vertex2 - правая
    private fun joining (vertex1: VertexBTree<K, V>, vertex2: VertexBTree<K, V>): VertexBTree<K, V> {
        var number: Int = -1
        for (i in 0 until vertex1.father!!.nodes.size) {
            if (vertex1.father!!.nodes[i].leftChild == vertex1) {
                number = i
                break
            }
        }
        if (number != vertex1.father!!.nodes.size-1)
            vertex1.father!!.nodes[number+1].leftChild = vertex1.father!!.nodes[number].leftChild
        vertex1.nodes.add(NodeBTree(vertex1.father!!.nodes[number].key, vertex1.father!!.nodes[number].value))
        vertex1.nodes.last().leftChild = vertex1.nodes[vertex1.nodes.size-2].rightChild
        vertex1.nodes.last().rightChild = vertex2.nodes[0].leftChild
        for (i in 0 until vertex2.nodes.size) {
            vertex2.nodes[i].leftChild?.father = vertex1
            vertex2.nodes[i].rightChild?.father = vertex1
        }
        vertex1.nodes.addAll(vertex2.nodes)
        vertex1.father!!.nodes.removeAt(number)
        if (vertex1.father == root && vertex1.father!!.nodes.size == 0) {
            root = vertex1
            root!!.father = null
        }
        return vertex1
    }

    fun printBTree() {
        print(mutableListOf(root!!), root!!, 0)
        println()
    }
    private fun print (line: MutableList<VertexBTree<K, V>>, v: VertexBTree<K, V>, i: Int) {
        var k: Int
        if (i < 0) k = line.size-1
        else k = i
        line.removeAt(0)
        k--
        for (element in v.nodes) {
            if (element.leftChild == null) break
            line.add(element.leftChild!!)
            if (element == v.nodes.last()) line.add(element.rightChild!!)
        }

        print("[")
        for (element in v.nodes) print("${element.key}, ")
        print("]     ")

        if (k < 0) println()
        if (line.size > 0) print(line, line[0],k)
    }

}
