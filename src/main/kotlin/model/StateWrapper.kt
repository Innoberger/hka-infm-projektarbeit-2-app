package model

class StateWrapper {
    companion object {
        fun makeState(city: String, name: String): Node {
            val cityNode = Node.makeNode(city)
            val stateNode = Node.makeNode(name)

            Node.union(cityNode, stateNode)

            return cityNode
        }

        fun union(stateA: Node, stateB: Node) {
            Node.union(stateA, stateB)
        }
    }
}