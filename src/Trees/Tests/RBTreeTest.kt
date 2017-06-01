import Trees.RBTree.NodeRBTree
import Trees.RBTree.RBTree
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class RBTreeTest {

    fun IdealTree(tree: RBTree<Int, Int>) {
        tree.root = NodeRBTree(21, 21)
        tree.root!!.rightChild = NodeRBTree(27, 27)
        tree.root!!.rightChild!!.father = tree.root
        tree.root!!.leftChild = NodeRBTree(13, 13)
        tree.root!!.leftChild!!.color = true
        tree.root!!.leftChild!!.father = tree.root
        tree.root!!.leftChild!!.rightChild = NodeRBTree(15, 15)
        tree.root!!.leftChild!!.rightChild!!.father = tree.root!!.leftChild
        tree.root!!.leftChild!!.leftChild = NodeRBTree(5, 5)
        tree.root!!.leftChild!!.leftChild!!.father = tree.root!!.leftChild
        tree.root!!.leftChild!!.leftChild!!.rightChild = NodeRBTree(8, 8)
        tree.root!!.leftChild!!.leftChild!!.rightChild!!.color = true
        tree.root!!.leftChild!!.leftChild!!.rightChild!!.father = tree.root!!.leftChild!!.leftChild
        tree.root!!.leftChild!!.leftChild!!.leftChild = NodeRBTree(3, 3)
        tree.root!!.leftChild!!.leftChild!!.leftChild!!.color = true
        tree.root!!.leftChild!!.leftChild!!.leftChild!!.father = tree.root!!.leftChild!!.leftChild
    }

    @Test
    fun isItRbTree() {
        val tree = RBTree<Int, Int>()
        IdealTree(tree)
        assertEquals(true, Trees.Tests.isItRbTree(tree))
    }

    @Test
    fun insert() {
        val tree = RBTree<Int, Int>()
        for (i in 0..1000 step 7) {
            tree.insert(i, i)
            assertEquals(true, Trees.Tests.isItRbTree(tree))
        }
        for (i in 2000 downTo 1001 step 9) {
            tree.insert(i, i)
            assertEquals(true, Trees.Tests.isItRbTree(tree))
        }
    }


    @Test
    fun remove() {
        val tree = RBTree<Int, Int>()
        for (i in 0..1000 step 7) tree.insert(i, i)
        for (i in 2000 downTo 0) {
            tree.remove(i)
            assertEquals(true, Trees.Tests.isItRbTree(tree))
        }
        // Удаление вершины из идеального дерева
        IdealTree(tree)
        tree.remove(tree.root!!.key)
        assertEquals(true, Trees.Tests.isItRbTree(tree))
        assertEquals(13, tree.root?.key)
    }

    @Test
    fun find() {
        val tree = RBTree<Int, Int>()
        for (i in 0..1000 step 14) tree.insert(i, i)
        // Эти значения есть в дереве
        assertEquals(0, tree.find(0)?.key)
        assertEquals(28, tree.find(28)?.key)
        assertEquals(224, tree.find(224)?.key)
        //Следующих значений нет в дереве
        assertEquals(null, tree.find(13)?.key)
        assertEquals(null, tree.find(204)?.key)
        assertEquals(null, tree.find(177)?.key)

        //Далее удаляем все вершины и делаем поиск в пустом дереве
        for (i in 0..1000) tree.remove(i)
        // Проверка, что дерево пустое
        assertEquals(null, tree.root)
        // Собственно, вот и сама проверка
        assertEquals(null, tree.find(0)?.key)
        assertEquals(null, tree.find(28)?.key)
        assertEquals(null, tree.find(224)?.key)
    }

    @Test
    fun stressTest() {
        val tree = RBTree<Int, Int>()
        for (i in 1..1000000 step 2) tree.insert(i, i)
        for (i in 1000000 downTo  1 step 3 ) tree.remove(i)
        assertEquals(true, Trees.Tests.isItRbTree(tree))
    }
}