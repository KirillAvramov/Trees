package Trees.Tests

import Trees.RBTree.NodeRBTree
import Trees.RBTree.RBTree

internal fun <K: Comparable<K>, V> isItRbTree(tree: RBTree<K, V>): Boolean {
    if (tree.root == null) return true
    return bHeight(tree.root!!) >= 0
}

private fun <K: Comparable<K>, V> bHeight(node: NodeRBTree<K, V>): Int//check
{
    var leftHeight = 0
    var rightHeight = 0
    if (node.leftChild != null) leftHeight = bHeight(node.leftChild!!)
    if (node.rightChild != null) rightHeight = bHeight(node.rightChild!!)
    if (leftHeight != rightHeight) {
        return -1
    }
    if (!node.color) leftHeight++
    return leftHeight
}
