import Trees.BST.BST
import Trees.BTree.BTree
import Trees.Interface.Tree
import Trees.RBTree.RBTree

fun main(args: Array<String>) {
    var tree: Tree<Int, Int, *>
    var key: Int?
    var value: Int?

    while (true) {
        println("""Choose kind of tree:
    1 - binary search tree
    2 - red-black tree
    3 - b-tree
    other symbols - end of program""")

        val c = readLine()

        when (c) {
            "1" -> {
                tree = BST<Int, Int>()
            }
            "2" -> {
                tree = RBTree<Int, Int>()
            }
            "3" -> {
                println("Enter the minimum degree")
                val t = readLine()!!.toInt()
                tree = BTree<Int, Int>(t)
            }
            else -> return
        }


        cycle@ while (true) {

            println("""Choose action:
    1 - enter <key value> //for stop enter ' . '
    2 - find value by key
    3 - delete value by key
    4 - print tree
    else - end work with tree""")

            val choice = readLine()

            when (choice) {
                "1" -> {
                    var string = readLine()

                    while (string != ".") {

                        if (string != null) {

                            try {

                                key = string.takeWhile { it in "-0123456789" }.toInt()
                                value = string.takeLastWhile { it in "-0123456789" }.toInt()

                            } catch (error: Throwable) {

                                println("Error: Illegal argument! Try again")
                                break

                            }

                            tree.insert(key, value)
                        }
                        string = readLine()

                    }
                }
                "2" -> {
                    key = readLine()?.toInt()
                    if (key != null) println(tree.find(key).toString())

                }
                "3" -> {

                    key = readLine()?.toInt()
                    if (key != null) tree.remove(key)

                }
                "4" -> {

                    if (tree is RBTree) tree.printRBTree()
                    if (tree is BTree) tree.printBTree()
                    if (tree is BST) tree.printBST()
                }

                else -> {
                    break@cycle
                }

            }
        }
    }

}