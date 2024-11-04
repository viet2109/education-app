import { ReactNode } from "react";
import { Navigate } from "react-router-dom";
import routers from "../configs/routers";

interface Props {
  isAuth: boolean;
  children: ReactNode;
}

function PrivateRoute(props: Props) {
  const { isAuth, children } = props;

  return <>{isAuth ? children : <Navigate to={routers.login}></Navigate>}</>;
}

export default PrivateRoute;
