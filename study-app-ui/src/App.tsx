import {BrowserRouter as Router, Route, Routes} from "react-router-dom";
import {privateRoutes, publicRoutes} from "./routes";


function App() {
    return (
        <Router>
            <Routes>
                {publicRoutes.map((route, index) => (
                    <Route path={route.path} key={index} element={
                        <route.layout>
                            <route.page/>
                        </route.layout>
                    }/>
                ))}

                {privateRoutes.map((route, index) => (
                    <Route path={route.path} key={index} element={
                        <route.layout>
                            <route.page/>
                        </route.layout>
                    }/>
                ))}
            </Routes>
        </Router>
    );
}

export default App
