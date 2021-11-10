import {Typography} from '@mui/material';
import {useEffect, useState} from 'react';
import {Navigate, Route, Routes, useSearchParams } from 'react-router-dom';
import MainPage from './MainPage';


function App() {
  const [token, setToken] = useState<string>();

  const TokenCallbackPage = () => {
    const [params] = useSearchParams();
    
    useEffect(() => {
      const token = params.get("token");
      if (token !== null) {
        setToken(token);
      }
    }, [params]);
    

    return (
      <>
        {token !== null ? (
            <Navigate to="/" />
          ) : (
            <Typography variant="h1">No token provided :(</Typography>
          )
        }
      </>
    );
  };

  return (
    <Routes>
      <Route path="/callback" element={<TokenCallbackPage />} />
      <Route path="/" element={<MainPage token={token} />} />
    </Routes>
  );
}

export default App;
