import { useEffect } from "react";
import { useSelector } from "react-redux";
import {
  Route,
  BrowserRouter as Router,
  Routes,
  useNavigate,
} from "react-router-dom";
import Loading from "./components/loading.tsx";
import PrivateRoute from "./components/privateRoute.tsx";
import ScrollToTop from "./components/scrollToTop.tsx";
import { RootState } from "./redux/store.ts";
import { privateRoutes, publicRoutes } from "./routes";
import { setNavigate } from "./utils/navigate.ts";

function App() {
  const loading = useSelector((state: RootState) => state.app.loading);
  const user = useSelector((state: RootState) => state.auth.user);

  return (
    <>
      <Router>
        <ScrollToTop />
        <NavigationProvider />
        <Routes>
          {publicRoutes.map((route, index) => (
            <Route
              path={route.path}
              key={index}
              element={
                <route.layout>
                  <route.page />
                </route.layout>
              }
            />
          ))}

          {privateRoutes.map((route, index) => (
            <Route
              path={route.path}
              key={index}
              element={
                <PrivateRoute isAuth={Boolean(user)}>
                  <route.layout>
                    <route.page />
                  </route.layout>
                </PrivateRoute>
              }
            />
          ))}
        </Routes>
      </Router>
      {loading && <Loading />}
    </>
  );
}
const NavigationProvider = () => {
  const navigate = useNavigate();

  useEffect(() => {
    setNavigate(navigate);
  }, [navigate]);

  return null;
};
export default App;
