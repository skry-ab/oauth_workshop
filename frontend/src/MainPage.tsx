import React, {useEffect, useState} from 'react';
import { Button, Card, CardContent, CardHeader, Container, Divider, List, ListItem, ListItemText, Stack, TextField, Typography} from '@mui/material';
import {Form, Formik} from 'formik';
import axios from 'axios';

interface Message {
  message: string;
  author: string;
}

type MessageItemProps = {
  message: Message;
};

const MessageItem: React.FC<MessageItemProps> = ({ message: { message, author } }) => {
  return (
    <ListItem>
      <ListItemText primary={message} secondary={author} />
    </ListItem>
  );
};


const API_HOST = "http://127.0.0.1:8080";
const MESSAGES_URL = `${API_HOST}/api/messages`;

type MainPageProps = {
  token?: string;
};

const MainPage: React.FC<MainPageProps> = ({ token }) => {
  const [messages, setMessages] = useState<Message[]>([]);

  useEffect(() => {
    fetchMessages();
  }, []);

  const fetchMessages = async () => {
    try {
      const { data } = await axios.get<Message[]>(MESSAGES_URL, {
        headers: {

        }
      });

      setMessages(data);
    } catch (e) {
      console.error('Error when loading messages', e);
    }
  };

  const postMessage = async (message: string) => {
    try {
      const { data } = await axios.post<Message>(MESSAGES_URL, {
          message
        }, {
          headers: {
            "Authorization": `Bearer ${token}`
        }
      });

      const newMessages = [...messages, data];
      setMessages(newMessages);

    } catch (e) {
      console.error('Error when posting message', e);
    }

    return Promise.resolve(null);
  };

  const PostMessageForm = () => {
    return (
        <Formik
          initialValues={{ message: '' }}
          onSubmit={async ({ message }) => postMessage(message)}
        >
          {
            ({ values, handleChange }) => (
              <Form>
                <Stack direction='row' alignItems='center' gap={1}>
                  <TextField
                    fullWidth={true}
                    name='message'
                    label='Message'
                    value={values.message}
                    onChange={handleChange}
                  />
                  <Button variant='contained' type='submit'>
                    Add
                  </Button>
                </Stack>
              </Form>
            )
          }
        </Formik>
    );
  };

  return (
      <Container>
        <Card sx={{ marginTop: "1em" }}>
          <CardHeader title='Shoutbox'/>
          <CardContent>
            {token !== undefined ? (
              <PostMessageForm />
            ) : (
              <Button variant='contained' onClick={() => {
                window.location.href = `${API_HOST}/login`
              }}>
                Login
              </Button>
            )}
            <Divider sx={{ margin: '1em' }} />
            <Typography variant='h6'>Messages:</Typography>
            <List>
              {messages.map((message, index) => <MessageItem key={index} message={message} />)}
            </List>
          </CardContent>
        </Card>
      </Container>
  );
};

export default MainPage;
