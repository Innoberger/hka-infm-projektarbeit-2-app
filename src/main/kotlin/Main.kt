
import com.charleskorn.kaml.Yaml
import config.Config
import kotlinx.serialization.decodeFromString
import simple.States
import java.io.File
import java.util.*

fun main() {
    val states = States()

    val configFile = File("src/main/resources/de.yaml")

    val config = configFile.bufferedReader().use {
        Yaml.default.decodeFromString<Config>(it.readText())
    }

    /*
     * We process each configured state.
     */
    config.states.forEach { state ->
        var stateName = state.name

        /*
         * If there's any city name and state name that are equal
         * (e.g. most city states like Berlin),
         * add a corresponding suffix to the stateName.
         * This is to ensure that all nodes are disjoint (unique).
         */
        if (state.cities.any { city -> city == stateName }) {
            stateName = "$stateName (Land)"
        }

        /*
         * For every state, we process every configured city.
         */
        state.cities.forEachIndexed { i, city ->
            var cityName = city

            /*
             * Like before, we need to ensure that all nodes are disjoint (unique).
             * This time the comparison is a bit more tricky,
             * and we modify the cityName.
             */
            if (stateName == "$cityName (Land)")
                cityName = "$cityName (Stadt)"

            when (i) {
                /*
                 * The 1st city (index = 0) is defined as the capital city of a state.
                 * We say it will be assigned to the "real" state instead of a dummy city.
                 */
                0 -> states.makeState(cityName, stateName)

                else -> {
                    /*
                     * For every other element, a dummy state for each city is created.
                     * Only the first in the list will get a pointer to it's 'real' state.
                     *
                     * UUID is used to ensure that every dummy state is disjoint (unique).
                     */
                    states.makeState(cityName, UUID.randomUUID().toString())

                    /*
                     * With states.find(cityName) we retrieve the root node of
                     * the city structure, which is a dummy state.
                     * We ensured that cityName was already created before,
                     * so we can use !! to ignore the nullable result.
                     */
                    val dummyState = states.find(cityName)!!

                    /*
                     * Now we can merge the dummyState of cityName with the real state,
                     * Keep in mind that the second argument will inherit/pass the node name.
                     */
                    states.union(dummyState, stateName)
                }
            }
        }
    }

    // temporary print out the disjoint-set forest
    println(states.states)

    // temporary hardcoded tests
    printSearchString(states, "Berlin (Stadt)")
    printSearchString(states, "Ingolstadt")
    printSearchString(states, "Karlsruhe")
    printSearchString(states, "Mannheim")
    printSearchString(states, "Potsdam")
    printSearchString(states, "Cottbus")
    printSearchString(states, "Bremen (Stadt)")
    printSearchString(states, "Bremerhaven")
}

fun printSearchString(states: States, city: String) {
    println("$city -> (...) -> ${states.find(city)}")
}